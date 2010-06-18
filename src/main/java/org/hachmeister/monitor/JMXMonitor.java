package org.hachmeister.monitor;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXMonitor {

	public static void main(String[] args) throws IOException {
		if (args.length < 2 || args.length > 3) {
			System.out.println("no Wrong count of parameters.");
			return;
		}

		String host = args[0];
		Integer port = null;

		try {
			port = Integer.valueOf(args[1]);
		} catch (Exception e) {
			System.out.println("no Port is not an integer.");
			return;
		}

		if (args.length == 3) {
			if ("config".equals(args[2])) {
				System.out.println("graph_title Tomcat (" + host + ":" + port + ")");
				System.out.println("graph_vlabel Memory (in Byte)");
				System.out.println("memory_heap_init.label Initial heap");
				System.out.println("memory_heap_max.label Max heap");
				System.out.println("memory_heap_committed.label Committed heap");
				System.out.println("memory_heap_used.label Used heap");
				System.out.println("memory_nonheap_init.label Initial nonheap");
				System.out.println("memory_nonheap_max.label Max nonheap");
				System.out.println("memory_nonheap_committed.label Committed nonheap");
				System.out.println("memory_nonheap_used.label Used nonheap");
			}
			return;
		}

		JMXMonitor monitor = new JMXMonitor();

		try {
			monitor.monitor(host, port);
		} catch (Exception e) {
			System.out.println("no " + e.getMessage());
		}

	}

	private void monitor(String host, Integer port) throws Exception {
		JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":"
				+ port + "/jmxrmi");
		JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
		MBeanServerConnection remote = connector.getMBeanServerConnection();
		MemoryMXBean memory = (MemoryMXBean) ManagementFactory.newPlatformMXBeanProxy(remote,
				ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);

		MemoryUsage memoryUsage = memory.getHeapMemoryUsage();

		System.out.println("memory_heap_init.value " + memoryUsage.getInit());
		System.out.println("memory_heap_max.value " + memoryUsage.getMax());
		System.out.println("memory_heap_committed.value " + memoryUsage.getCommitted());
		System.out.println("memory_heap_used.value " + memoryUsage.getUsed());

		memoryUsage = memory.getNonHeapMemoryUsage();

		System.out.println("memory_nonheap_init.value " + memoryUsage.getInit());
		System.out.println("memory_nonheap_max.value " + memoryUsage.getMax());
		System.out.println("memory_nonheap_committed.value " + memoryUsage.getCommitted());
		System.out.println("memory_nonheap_used.value " + memoryUsage.getUsed());

		connector.close();
	}
}
