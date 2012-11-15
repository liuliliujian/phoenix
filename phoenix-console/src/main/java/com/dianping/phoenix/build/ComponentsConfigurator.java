package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.console.service.DefaultDeployService;
import com.dianping.phoenix.console.service.DefaultGitService;
import com.dianping.phoenix.console.service.DefaultProjectService;
import com.dianping.phoenix.console.service.DefaultStatusReporter;
import com.dianping.phoenix.console.service.DefaultVersionManager;
import com.dianping.phoenix.console.service.DefaultVersionService;
import com.dianping.phoenix.console.service.DefaultWarService;
import com.dianping.phoenix.console.service.DeployService;
import com.dianping.phoenix.console.service.GitService;
import com.dianping.phoenix.console.service.ProjectService;
import com.dianping.phoenix.console.service.StatusReporter;
import com.dianping.phoenix.console.service.VersionManager;
import com.dianping.phoenix.console.service.VersionService;
import com.dianping.phoenix.console.service.WarService;
import com.site.lookup.configuration.AbstractResourceConfigurator;
import com.site.lookup.configuration.Component;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(StatusReporter.class, DefaultStatusReporter.class));
		all.add(C(WarService.class, DefaultWarService.class) //
		      .req(StatusReporter.class));
		all.add(C(GitService.class, DefaultGitService.class));
		all.add(C(VersionManager.class, DefaultVersionManager.class));
		all.add(C(VersionService.class, DefaultVersionService.class) //
		      .req(WarService.class, GitService.class, VersionManager.class));

		all.add(C(ProjectService.class, DefaultProjectService.class));
		all.add(C(DeployService.class, DefaultDeployService.class));

		// Please keep it as last
		all.addAll(new WebComponentConfigurator().defineComponents());

		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}
