package com.dianping.phoenix.agent.core;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.unidal.lookup.annotation.Inject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.processor.kernel.Config;
import com.dianping.phoenix.agent.response.entity.Container;
import com.dianping.phoenix.agent.response.entity.Domain;
import com.dianping.phoenix.agent.response.entity.Kernel;
import com.dianping.phoenix.agent.response.entity.Lib;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.entity.War;
import com.dianping.phoenix.agent.util.Artifact;
import com.dianping.phoenix.agent.util.ArtifactResolver;

public class AgentStatusReporter {

	@Inject
	private Config config;
	@Inject
	ScriptExecutor scriptExector;

	public AgentStatusReporter() {
	}

	public Response report() throws Exception {
		Response res = new Response();
		File serverXml = config.getServerXml();
		if(serverXml != null && serverXml.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(serverXml);
			NodeList ctxList = doc.getElementsByTagName("Context");
			// for each <Context>
			for (int i = 0; i < ctxList.getLength(); i++) {
				Element ctx = (Element) ctxList.item(i);
				String strDomainDocBase = ctx.getAttribute("docBase");
				NodeList loaderList = ctx.getElementsByTagName("Loader");
				String strKernelDocBase = null;
				if(loaderList.getLength() > 0) {
					Element loader = (Element) loaderList.item(0);
					strKernelDocBase = loader.getAttribute("kernelDocBase");
				}
				
				// kernel info
				War kernelWar = reportWar(strKernelDocBase);
				Kernel kernel = new Kernel();
				kernel.setWar(kernelWar);
				
				// domain info
				Domain domain = new Domain();
				War domainWar = reportWar(strDomainDocBase);
				domain.setKernel(kernel);
				domain.setWar(domainWar);
				
				res.addDomain(domain);
			}
		}
		
		Container container = new Container();
		container.setInstallPath(config.getContainerInstallPath());
		container.setName(config.getContainerType().toString().toLowerCase());
		// TODO
		container.setStatus("up");
		container.setVersion("6.0.35");
		res.setContainer(container);
		res.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
		
		return res;
	}

	/**
	 * Get full basic infos of war <code>warDocBase</code>
	 * @param warDocBase war docBase
	 * @return
	 */
	private War reportWar(File warDocBase) {
		
		if(warDocBase == null || !warDocBase.exists()) {
			return null;
		}
		
		War war = new War();
		ArtifactResolver resolver = new ArtifactResolver();
		Artifact artifactWar = resolver.resolve(warDocBase );
		
		if (artifactWar == null) {
			return null;
		} else {
			war.setName(artifactWar.getArtifactId());
			war.setVersion(artifactWar.getVersion());

			File libDir = new File(warDocBase, "WEB-INF/lib");
			File[] libs = libDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			});

			for (int i = 0; i < libs.length; i++) {
				Artifact artifactLibJar = resolver.resolve(libs[i]);
				Lib lib = new Lib();
				lib.setArtifactId(artifactLibJar.getArtifactId());
				lib.setGroupId(artifactLibJar.getGroupId());
				lib.setVersion(artifactLibJar.getVersion());
				war.addLib(lib);
			}

			return war;
		}
	}
	
	public War reportWar(String strWarDocBase) {
		if(strWarDocBase == null) {
			return null;
		} else {
			return reportWar(new File(strWarDocBase));
		}
	}
	
}
