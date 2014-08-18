/**
 * 
 */
package org.minnal.core.config;

import java.util.HashMap;
import java.util.Map;

import org.minnal.core.BundleConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ganeshs
 *
 */
public class ContainerConfiguration extends Configuration {

	@JsonProperty("server")
	private ServerConfiguration serverConfiguration;
	
	@JsonProperty(required=true)
	private Map<String, String> mounts = new HashMap<String, String>();
	
	private String basePath = "/";
	
	private Map<String, BundleConfiguration> bundleOverrides = new HashMap<String, BundleConfiguration>();
	
	public ContainerConfiguration() {
	}
	
	public ContainerConfiguration(String name) {
		super(name);
	}

	/**
	 * @return the serverConfiguration
	 */
	public ServerConfiguration getServerConfiguration() {
		return serverConfiguration;
	}

	/**
	 * @param serverConfiguration the serverConfiguration to set
	 */
	public void setServerConfiguration(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	/**
	 * @return the mounts
	 */
	public Map<String, String> getMounts() {
		return mounts;
	}

	/**
	 * @param mounts the mounts to set
	 */
	public void setMounts(Map<String, String> mounts) {
		this.mounts = mounts;
	}

	/**
	 * @return the bundleOverrides
	 */
	public Map<String, BundleConfiguration> getBundleOverrides() {
		return bundleOverrides;
	}

	/**
	 * @param bundleOverrides the bundleOverrides to set
	 */
	public void setBundleOverrides(Map<String, BundleConfiguration> bundleOverrides) {
		this.bundleOverrides = bundleOverrides;
	}

	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
}
