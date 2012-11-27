package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.agent.core.Agent;
import com.dianping.phoenix.agent.core.DefaultAgent;
import com.dianping.phoenix.agent.core.shell.DefaultScriptExecutor;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessorFactory;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployStep;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployStepContext;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTaskProcessor;
import com.dianping.phoenix.agent.core.tx.FileBasedTransactionManager;
import com.dianping.phoenix.agent.core.tx.TransactionManager;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(DeployStep.Context.class, DeployStepContext.class).req(ScriptExecutor.class));
		all.add(C(TransactionManager.class, FileBasedTransactionManager.class));
		all.add(C(ScriptExecutor.class, DefaultScriptExecutor.class));
		all.add(C(Agent.class, DefaultAgent.class).req(TransactionManager.class).req(TaskProcessorFactory.class));
		all.add(C(TaskProcessor.class, DeployTaskProcessor.class).req(TransactionManager.class).req(DeployStep.Context.class));
		all.add(C(TaskProcessorFactory.class));
		all.add(C(TransactionManager.class, FileBasedTransactionManager.class));

		// Please keep it as last
		all.addAll(new WebComponentConfigurator().defineComponents());

		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}
