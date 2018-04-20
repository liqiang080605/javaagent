package com.jd.jvm.jmonitor.core.server.jetty;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

import com.jd.jvm.jmonitor.core.CoreConfigure;
import com.jd.jvm.jmonitor.core.annotation.Http;
import com.jd.jvm.jmonitor.core.model.Module;
import com.jd.jvm.jmonitor.core.server.CoreServer;
import com.jd.jvm.jmonitor.core.server.DefaultModuleResourceManager;
import com.jd.jvm.jmonitor.core.server.ModuleResourceManager;
import com.jd.jvm.jmonitor.core.server.jetty.servlet.ModuleHttpServlet;
import com.jd.jvm.jmonitor.core.util.ClassUtils;
import com.jd.jvm.jmonitor.core.util.Initializer;
import com.jd.jvm.jmonitor.core.util.NetworkUtils;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

/**
 * Jetty实现的Http服务器
 *
 */
public class JettyCoreServer implements CoreServer {

    private static final String HTTP_CLASS_PACKAGE = "com.jd.jvm.jmonitor.core.collector";
	private static volatile CoreServer coreServer;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    // 初始化器
    private final Initializer initializer = new Initializer(true);

    // HTTP服务器
    private Server httpServer;
	private ModuleResourceManager moduleResourceManager;
    
    private static Map<String, Module> httpClassMap = new ConcurrentHashMap<String, Module>();

    /**
     * 单例
     *
     * @return CoreServer单例
     */
    public static CoreServer getInstance() {
        if (null == coreServer) {
            synchronized (CoreServer.class) {
                if (null == coreServer) {
                    coreServer = new JettyCoreServer();
                }
            }
        }
        return coreServer;
    }

    public static Map<String, Module> getHttpClassMap() {
		return httpClassMap;
	}

	public boolean isBind() {
        return initializer.isInitialized();
    }

    public void unbind() throws IOException {
        try {
            initializer.destroyProcess(new Initializer.Processor() {
                public void process() throws Throwable {
                    if (null != httpServer) {

                        // stop http server
                        httpServer.stop();
//                        while (!httpServer.isStopped()) {
//                            logger.info("server is stopping....");
//                            Thread.sleep(1000);
//                        }
                        logger.info("server was stop.");

                    }
                }
            });

            // destroy http server
            httpServer.destroy();
            logger.info("server was destroyed.");

        } catch (Throwable cause) {
            logger.debug("unBind failed.", cause);
            throw new IOException("unBind failed.", cause);
        }
    }

    public InetSocketAddress getLocal() throws IOException {
        if (!isBind()
                || null == httpServer) {
            throw new IOException("server was not bind yet.");
        }

        SelectChannelConnector scc = null;
        final Connector[] connectorArray = httpServer.getConnectors();
        if (null != connectorArray) {
            for (final Connector connector : connectorArray) {
                if (connector instanceof SelectChannelConnector) {
                    scc = (SelectChannelConnector) connector;
                    break;
                }//if
            }//for
        }//if

        if (null == scc) {
            throw new IllegalStateException("not found SelectChannelConnector");
        }

        return new InetSocketAddress(
                scc.getHost(),
                scc.getLocalPort()
        );
    }

    /*
     * 初始化Jetty's ContextHandler
     */
    private void initJettyContextHandler() {
        final ServletContextHandler context = new ServletContextHandler(SESSIONS);

        // websocket-servlet
        //context.addServlet(new ServletHolder(new WebSocketAcceptorServlet(coreModuleManager, moduleResourceManager)), "/module/websocket/*");

        // module-http-servlet
        context.addServlet(new ServletHolder(new ModuleHttpServlet(moduleResourceManager)), "/*");

        if(CoreConfigure.getInstance().getType().equals("tomcat")) {
        	context.setContextPath("/monitorTomcat");
        } else {
        	context.setContextPath("/monitorJvm");
        }
        
        context.setClassLoader(getClass().getClassLoader());
        httpServer.setHandler(context);
    }

    /*
     * 初始化Logback日志配置
     */
    private void initLogback(final CoreConfigure cfg) throws Throwable {
        final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        loggerContext.reset();
        InputStream is = null;
        try {
            is = new FileInputStream(new File(cfg.getCfgLibPath() + File.separator + "jmonitor-logback.xml"));
            configurator.doConfigure(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private void initHttpServer(final CoreConfigure cfg) {

        // 如果IP:PORT已经被占用，则无法继续被绑定
        // 这里说明下为什么要这么无聊加个这个判断，让Jetty的Server.bind()抛出异常不是更好么？
        // 比较郁闷的是，如果这个端口的绑定是"SO_REUSEADDR"端口可重用的模式，那么这个server是能正常启动，但无法正常工作的
        // 所以这里必须先主动检查一次端口占用情况，当然了，这里也会存在一定的并发问题，BUT，我认为这种概率事件我可以选择暂时忽略
        if (NetworkUtils.isPortInUsing(cfg.getServerIp(), cfg.getServerPort())) {
            throw new IllegalStateException(String.format("server[ip=%s;port=%s;] already in using, server bind failed.",
                    cfg.getServerIp(), cfg.getServerPort()));
        }

        httpServer = new Server(new InetSocketAddress(cfg.getServerPort()));
        if (httpServer.getThreadPool() instanceof QueuedThreadPool) {
            final QueuedThreadPool qtp = (QueuedThreadPool) httpServer.getThreadPool();
            qtp.setName("jmonitor-jetty-qtp" + qtp.hashCode());
        }
    }

    public synchronized void bind(final CoreConfigure cfg, final Instrumentation inst) throws IOException {
        try {
        	
        	loadHttpClassMap();
        	
            initializer.initProcess(new Initializer.Processor() {
                public void process() throws Throwable {

                    initLogback(cfg);
                    logger.debug("init logback finished.");
                    logger.info("cfg={}", cfg.toString());

                    initManager(inst, cfg);

                    initHttpServer(cfg);
                    logger.debug("init http-server finished.");

                    initJettyContextHandler();
                    logger.debug("init servlet finished.");

                    httpServer.start();
                    logger.debug("http-server started.");

                    logger.info("jmonitor start finished.");

                }
				
            });
        } catch (Throwable cause) {
            logger.warn("server bind failed. cfg={}", cfg, cause);
            throw new IOException(
                    String.format("server bind to %s:%s failed.", cfg.getServerIp(), cfg.getServerPort()),
                    cause
            );
        }

        logger.info("server bind to {} success. cfg={}", getLocal(), cfg);
    }
    
    // 初始化各种manager
    private void initManager(final Instrumentation inst,
                             final CoreConfigure cfg) {
        // 初始化模块资源管理器
        this.moduleResourceManager = new DefaultModuleResourceManager();
    }

    private void loadHttpClassMap() {
    	Set<Class<?>> classSet = ClassUtils.getClassSet(getClass().getClassLoader(), HTTP_CLASS_PACKAGE);
    	
    	for(Class<?> clazz : classSet) {
    		Annotation annotation = clazz.getAnnotation(Http.class);
    		if(annotation != null && annotation instanceof Http) {
    			Http http = (Http) annotation;
    			
    			try {
	    			Module module = new Module();
	    			module.setClassLoader(getClass().getClassLoader());
	    			module.setClazz(clazz);
					module.setObj(clazz.newInstance());
					httpClassMap.put(http.value(), module);
				} catch (Exception e) {
					logger.error("New module instance failed. Exception is ", e);
				}
    			
    		}
    	}
	}

	public void destroy() {
        if (isBind()) {
            try {
                unbind();
            } catch (IOException e) {
                logger.warn("nnBind failed when destroy.", e);
            }
        }
        if (null != httpServer) {
            httpServer.destroy();
        }
    }
}
