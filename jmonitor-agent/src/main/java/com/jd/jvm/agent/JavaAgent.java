package com.jd.jvm.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.util.jar.JarFile;

public class JavaAgent {
	private static final BootLogger logger = BootLogger.getLogger(JavaAgent.class.getName());
	
    private static String substringBeforeLast(String str, String separator) {
    	return str.substring(0, str.lastIndexOf(separator));
    }
	
	// jmonitor主目录
    private static final String JMONITOR_HOME
            = substringBeforeLast(JavaAgent.class.getProtectionDomain().getCodeSource().getLocation().getFile(), File.separator)
            + File.separator + "..";

    // jmonitor配置文件目录
    private static final String JMONITOR_CONF_PATH
            = JMONITOR_HOME + File.separatorChar + "conf";

    // 模块目录
    private static final String JMONITOR_MODULE_PATH
            = JMONITOR_HOME + File.separatorChar + "module";

    private static final String JMONITOR_USER_MODULE_PATH
            = System.getProperties().getProperty("user.home")
            + File.separator + ".jmonitor-module";

    // jmonitor核心工程文件
    private static final String JMONITOR_CORE_JAR_PATH
            = JMONITOR_HOME + File.separatorChar + "lib" + File.separator + "jmonitor-core.jar";

    // jmonitor-spy工程文件
    private static final String JMONITOR_SPY_JAR_PATH
            = JMONITOR_HOME + File.separatorChar + "lib" + File.separator + "jmonitor-spy.jar";

    private static final String JMONITOR_PROPERTIES_PATH
            = JMONITOR_CONF_PATH + File.separator + "jmonitor.properties";

    // jmonitor-provider库目录
    private static final String JMONITOR_PROVIDER_LIB_PATH
            = JMONITOR_HOME + File.separatorChar + "provider";

    // 启动模式: agent方式加载
    private static final String LAUNCH_MODE_AGENT = "agent";

    // 启动模式: attach方式加载
    private static final String LAUNCH_MODE_ATTACH = "attach";

    // agentmain上来的结果输出到文件${HOME}/.jmonitor.token
    private static final String RESULT_FILE_PATH = System.getProperties().getProperty("user.home")
            + File.separator + ".jmonitor.token";

    private static final String CLASS_OF_CORE_CONFIGURE = "com.jd.jvm.jmonitor.core.CoreConfigure";
    private static final String CLASS_OF_JETTY_CORE_SERVER = "com.jd.jvm.jmonitor.core.server.jetty.JettyCoreServer";

    // 全局持有ClassLoader用于隔离jmonitor实现
    private static volatile ClassLoader jmonitorClassLoader;
	
    private static String getDefaultString(final String string, final String defaultString) {
        return isNotBlankString(string)
                ? string
                : defaultString;
    }
    
    private static boolean isNotBlankString(final String string) {
        return null != string
                && string.length() > 0
                && !string.matches("^\\s*$");
    }
    
    /**
     * 启动加载
     *
     * @param propertiesFilePath 配置文件路径
     * @param inst               inst
     */
	public static void premain(String propertiesFilePath, Instrumentation inst) {
		main(JMONITOR_CORE_JAR_PATH,
                String.format(";conf=%s;system_module=%s;mode=%s;jmonitor_home=%s;user_module=%s;provider=%s;",
                		JMONITOR_CONF_PATH, JMONITOR_MODULE_PATH, LAUNCH_MODE_AGENT, JMONITOR_HOME, JMONITOR_USER_MODULE_PATH, JMONITOR_PROVIDER_LIB_PATH),
                getDefaultString(propertiesFilePath, JMONITOR_PROPERTIES_PATH),
                inst);
	}

	private static synchronized InetSocketAddress main(final String coreJarPath,
            final String coreFeatureString,
            final String propertiesFilePath,
            final Instrumentation inst) {
		try {
			// 将Spy注入到BootstrapClassLoader
			//inst.appendToBootstrapClassLoaderSearch(new JarFile(new File(JMONITOR_SPY_JAR_PATH)));
			
			// 构造自定义的类加载器，尽量减少jmonitor对现有工程的侵蚀
			final ClassLoader agentLoader = loadOrDefineClassLoader(coreJarPath);
			
			// CoreConfigure类定义
			final Class<?> classOfConfigure = agentLoader.loadClass(CLASS_OF_CORE_CONFIGURE);
			
			
			// 反序列化成CoreConfigure类实例
			final Object objectOfCoreConfigure = classOfConfigure.getMethod("toConfigure", String.class, String.class)
			.invoke(null, coreFeatureString, propertiesFilePath);
			logger.error("ClassLoader : " + objectOfCoreConfigure.getClass().getClassLoader().getParent());
			
			// JtServer类定义
			final Class<?> classOfJtServer = agentLoader.loadClass(CLASS_OF_JETTY_CORE_SERVER);
			
			// 获取JtServer单例
			final Object objectOfJtServer = classOfJtServer.getMethod("getInstance").invoke(null);
			
			// gaServer.isBind()
			final boolean isBind = (Boolean) classOfJtServer.getMethod("isBind").invoke(objectOfJtServer);
			
			
			// 如果未绑定,则需要绑定一个地址
			if (!isBind) {
				try {
					classOfJtServer.getMethod("bind", classOfConfigure, Instrumentation.class)
					.invoke(objectOfJtServer, objectOfCoreConfigure, inst);
				} catch (Throwable t) {
					classOfJtServer.getMethod("destroy").invoke(objectOfJtServer);
					throw t;
				}
			}
			
			// 返回服务器绑定的地址
			return (InetSocketAddress) classOfJtServer.getMethod("getLocal").invoke(objectOfJtServer);
		} catch (Throwable cause) {
			throw new RuntimeException("jmonitor attach failed.", cause);
		}
	}
	
	private static ClassLoader loadOrDefineClassLoader(String coreJar) throws Throwable {
        final ClassLoader classLoader;

        // 如果已经被启动则返回之前启动的ClassLoader
        if (null != jmonitorClassLoader) {
            classLoader = jmonitorClassLoader;
        }

        // 如果未启动则重新加载
        else {
            classLoader = new JmonitorClassLoader(coreJar);
        }

        return jmonitorClassLoader = classLoader;
    }
	
	public static ClassLoader getClassLoader() {
		return jmonitorClassLoader;
	}
}