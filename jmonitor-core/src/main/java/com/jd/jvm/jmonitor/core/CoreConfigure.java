package com.jd.jvm.jmonitor.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.jd.jvm.jmonitor.core.annotation.Information;
import com.jd.jvm.jmonitor.core.util.FeatureCodec;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 内核启动配置
 */
public class CoreConfigure {

    private static final String KEY_JMONITOR_HOME = "jmonitor_home";
    private static final String KEY_LAUNCH_MODE = "mode";
    private static final String KEY_SERVER_IP = "agent.ip";
    private static final String KEY_SERVER_PORT = "agent.port";
    private static final String KEY_SERVER_NAME = "agent.name";
    private static final String KEY_SERVER_TYPE = "agent.type";

    private static final String KEY_SYSTEM_MODULE_LIB_PATH = "system_module";
    private static final String KEY_CFG_LIB_PATH = "conf";
    private static final String VAL_LAUNCH_MODE_AGENT = "agent";
    private static final String VAL_LAUNCH_MODE_ATTACH = "attach";
    // 受保护key数组，在保护key范围之内，如果前端已经传递过参数了，只能认前端，后端无法修改
    private static final String[] PROTECT_KEY_ARRAY = {KEY_JMONITOR_HOME, KEY_LAUNCH_MODE, KEY_SERVER_IP, KEY_SERVER_PORT};

    private static final FeatureCodec codec = new FeatureCodec(';', '=');

    private final Map<String, String> featureMap;

    private CoreConfigure(final String featureString) {
        this.featureMap = codec.toMap(featureString);
    }

    private static volatile CoreConfigure instance;

    public static CoreConfigure toConfigure(final String featureString, final String propertiesFilePath) {
        return instance = mergePropertiesFile(new CoreConfigure(featureString), propertiesFilePath);
    }

    // 从配置文件中合并配置到CoreConfigure中
    private static CoreConfigure mergePropertiesFile(final CoreConfigure cfg, final String propertiesFilePath) {
        cfg.featureMap.putAll(propertiesToStringMap(fetchProperties(propertiesFilePath)));
        
        if(System.getProperty("agent.type") != null) {
        	cfg.featureMap.put("agent.type", System.getProperty("agent.type"));
        }
        
        if(System.getProperty("agent.port") != null) {
        	cfg.featureMap.put("agent.port", System.getProperty("agent.port"));
        }
        
        if(System.getProperty("agent.name") != null) {
        	cfg.featureMap.put("agent.name", System.getProperty("agent.name"));
        }
        
        return cfg;
    }

    // 从指定配置文件路径中获取配置信息
    private static Properties fetchProperties(final String propertiesFilePath) {
        final Properties properties = new Properties();
        InputStream is = null;
        try {
            is = FileUtils.openInputStream(new File(propertiesFilePath));
            properties.load(is);
        } catch (Throwable cause) {
            // cause.printStackTrace(System.err);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return properties;
    }

    // 配置转map
    private static Map<String, String> propertiesToStringMap(final Properties properties) {
        final Map<String, String> map = new HashMap<String, String>();
        for (String key : properties.stringPropertyNames()) {

            // 过滤掉受保护的key
            if (ArrayUtils.contains(PROTECT_KEY_ARRAY, key)
                    && map.containsKey(key)) {
                continue;
            }

            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    public static CoreConfigure getInstance() {
        return instance;
    }


    /**
     * 获取系统模块加载路径
     *
     * @return 模块加载路径
     */
    public String getSystemModuleLibPath() {
        return featureMap.get(KEY_SYSTEM_MODULE_LIB_PATH);
    }

    /**
     * 获取配置文件加载路径
     *
     * @return 配置文件加载路径
     */
    public String getCfgLibPath() {
        return featureMap.get(KEY_CFG_LIB_PATH);
    }

    @Override
    public String toString() {
        return codec.toString(featureMap);
    }

    /**
     * 是否以Agent模式启动
     *
     * @return true/false
     */
    private boolean isLaunchByAgentMode() {
        return StringUtils.equals(featureMap.get(KEY_LAUNCH_MODE), VAL_LAUNCH_MODE_AGENT);
    }

    /**
     * 是否以Attach模式启动
     *
     * @return true/false
     */
    private boolean isLaunchByAttachMode() {
        return StringUtils.equals(featureMap.get(KEY_LAUNCH_MODE), VAL_LAUNCH_MODE_ATTACH);
    }

    /**
     * 获取jmonitor的启动模式
     * 默认按照ATTACH模式启动
     *
     * @return jmonitor的启动模式
     */
    public Information.Mode getLaunchMode() {
        if (isLaunchByAgentMode()) {
            return Information.Mode.AGENT;
        }
        if (isLaunchByAttachMode()) {
            return Information.Mode.ATTACH;
        }
        return Information.Mode.ATTACH;
    }

    /**
     * 获取jmonitor安装目录
     *
     * @return jmonitor安装目录
     */
    public String getJvmJmonitorHome() {
        return featureMap.get(KEY_JMONITOR_HOME);
    }

    /**
     * 获取服务器绑定IP
     *
     * @return 服务器绑定IP
     */
    public String getServerIp() {
        return StringUtils.isNotBlank(featureMap.get(KEY_SERVER_IP))
                ? featureMap.get(KEY_SERVER_IP)
                : "127.0.0.1";
    }

    /**
     * 获取服务器端口
     *
     * @return 服务器端口
     */
    public int getServerPort() {
        return NumberUtils.toInt(featureMap.get(KEY_SERVER_PORT), 0);
    }
    
    /**
     * 获取agent的名字
     * 
     * @return
     */
    public String getAgentName() {
    	return StringUtils.isNotBlank(featureMap.get(KEY_SERVER_NAME)) 
    			? featureMap.get("agent.name") : "agent-jmonitor";
    }
    
    /**
     * 获取监控的类型（支持tomcat、jvm）
     * 
     * @return
     */
    public String getType() {
    	return StringUtils.isNotBlank(featureMap.get(KEY_SERVER_TYPE)) 
    			? featureMap.get("agent.type") : "jvm";
    }

}
