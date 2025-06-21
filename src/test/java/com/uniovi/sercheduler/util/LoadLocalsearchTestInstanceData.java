package com.uniovi.sercheduler.util;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.parser.HostFileLoader;
import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowFileLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class LoadLocalsearchTestInstanceData {


    public static InstanceData loadNeighborhoodOperatorsTest(){
        return loadTestJson("localsearch/hosts.json", "localsearch/workflow.json");
    }

    private static InstanceData loadTestJson(String hostFile, String workflowFile) {
        try {
            HostLoader hostLoader = new HostFileLoader();
            WorkflowLoader workflowLoader = new WorkflowFileLoader();

            var hostsJson = new ClassPathResource(hostFile).getFile();
            var hostsDao = hostLoader.readFromFile(hostsJson);
            var hosts = hostLoader.load(hostsDao);

            var workflowJson = new ClassPathResource(workflowFile).getFile();
            var workflowDao = workflowLoader.readFromFile(workflowJson);
            var workflow = workflowLoader.load(workflowDao);

            return new InstanceData(workflow, hosts,UnitParser.parseUnits("441Gf"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
