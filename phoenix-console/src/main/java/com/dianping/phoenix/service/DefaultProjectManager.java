package com.dianping.phoenix.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.project.entity.Project;
import com.dianping.phoenix.project.entity.Root;
import com.dianping.phoenix.project.transform.DefaultSaxParser;

public class DefaultProjectManager implements ProjectManager, Initializable {
	@Inject
	private DeployManager m_deployManager;

	private Root m_root;

	private Map<String, Integer> m_map = new HashMap<String, Integer>();

	@Override
	public List<Project> searchProjects(String keyword) throws Exception {
		List<Project> list = new ArrayList<Project>();

		if (keyword == null || keyword.trim().length() == 0) {
			list.addAll(m_root.getProjects().values());
		} else {
			for (Map.Entry<String, Project> e : m_root.getProjects().entrySet()) {
				if (e.getKey().contains(keyword)) {
					list.add(e.getValue());
				}
			}
		}

		return list;
	}

	@Override
	public Project findProjectBy(String name) throws Exception {
		return m_root.findProject(name);
	}

	@Override
	public int deployToProject(String name, List<String> hosts, DeployPlan plan) throws Exception {
		if (m_map.containsKey(name)) {
			throw new RuntimeException(String.format("Project(%s) is being rolling out!", name));
		}

		return m_deployManager.deploy(name, hosts, plan);
	}

	@Override
	public void initialize() throws InitializationException {
		// TODO test purpose
		InputStream in = getClass().getResourceAsStream("/com/dianping/phoenix/deploy/project.xml");

		try {
			m_root = DefaultSaxParser.parse(in);
		} catch (Exception e) {
			throw new RuntimeException(
			      "Unable to load deploy model from resource(com/dianping/phoenix/deploy/project.xml)!", e);
		}
	}
}
