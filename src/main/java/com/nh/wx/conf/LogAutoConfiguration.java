package com.nh.wx.conf;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;

import com.nh.micro.logutil.LogUtil;



/**
 * 
 * @author ninghao
 *
 */
@Configuration
public class LogAutoConfiguration {
	@Autowired
	Environment env;

	@Value("#{logDirStr}")
	private String rootDir;
	@Value("${logutil.startDebug:true}")
	private String startDebug;

	@PostConstruct
	public void init() {
		Boolean debugFlag = Boolean.valueOf(startDebug);
		LogUtil.initLogContext(rootDir, debugFlag);
	}

	@Bean
	public String logDirStr() {
		String dir = env.getProperty("logutil.rootDir");
		// String dir="";
		if (dir == null || "".equals(dir)) {
			try {

				File path = new File(ResourceUtils.getURL("classpath:").getPath());
				String curPath = path.getParent();
				if (curPath.contains("!")) {
					String[] arr = curPath.split("!");
					String tmpPath = arr[0];
					File tmpFile = new File(tmpPath);
					dir = tmpFile.getParent();
				} else {
					dir = curPath;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (dir != null && dir.startsWith("file:")) {
			dir = dir.substring(6);
		}
		return dir;

	}
}
