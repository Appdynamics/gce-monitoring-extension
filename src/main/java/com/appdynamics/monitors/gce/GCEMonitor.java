
/*
 * Copyright 2014. AppDynamics LLC and its affiliates.
 *  All Rights Reserved.
 *  This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *  The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.monitors.gce;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.DisksScopedList;
import com.google.api.services.compute.model.Quota;
import com.google.api.services.compute.model.Region;
import com.google.common.base.Strings;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class GCEMonitor extends AManagedMonitor {

    private static final Logger LOG = Logger.getLogger(GCEMonitor.class);

    private static final String metricPathPrefix = "Custom Metrics|GCE|";
    public static final String APPLICATION_NAME = "AppD-GCEExtension/1.0";
    public static final String SCOPE_READ_ONLY = "https://www.googleapis.com/auth/compute.readonly";


    public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {

        String details=GCEMonitor.class.getPackage().getImplementationTitle();
        String msg = "Using Monitor Version ["+details+"]";
        if (LOG.isDebugEnabled()) {
            LOG.debug("Starting GCE Monitor");
            LOG.debug(msg);
            
        }

        String serviceAccountId = taskArguments.get("ServiceAccountId");
        String serviceAccountP12FilePath = taskArguments.get("ServiceAccountP12FilePath");
        String projectId = taskArguments.get("ProjectID");

        if (Strings.isNullOrEmpty(serviceAccountId) || Strings.isNullOrEmpty(serviceAccountP12FilePath) || Strings.isNullOrEmpty(projectId)) {
            LOG.error("Please configure all the mandatory details in monitor .xml");
            throw new RuntimeException("Please configure all the mandatory details in monitor .xml");
        }

        Compute googleCompute = createGoogleCompute(serviceAccountId, serviceAccountP12FilePath);

            //Project Stats
            List<Quota> projectStats = GCEStats.getProjectStats(googleCompute, projectId);
            if (projectStats != null) {
                for (Quota quota : projectStats) {
                    printMetric(metricPathPrefix, projectId + "|Quota|" + quota.getMetric() + "|Limit", quota.getLimit());
                    printMetric(metricPathPrefix, projectId + "|Quota|" + quota.getMetric() + "|Usage", quota.getUsage());
                }
            }

            //Region Stats
            List<Region> regionStats = GCEStats.getRegionStats(googleCompute, projectId);
            if (regionStats != null) {
                for (Region region : regionStats) {
                    List<Quota> quotas = region.getQuotas();
                    if (quotas != null) {
                        for (Quota quota : quotas) {
                            printMetric(metricPathPrefix, projectId + "|Region|" + region.getName() + "|Quota|" + quota.getMetric() + "|Limit", quota.getLimit());
                            printMetric(metricPathPrefix, projectId + "|Region|" + region.getName() + "|Quota|" + quota.getMetric() + "|Usage", quota.getUsage());
                        }
                    }
                }
            }

            //Disk Stats
            Map<String, DisksScopedList> diskStats = GCEStats.getDiskStats(googleCompute, projectId);
            if (diskStats != null) {
                for (Map.Entry<String, DisksScopedList> diskEntries : diskStats.entrySet()) {
                    String region = diskEntries.getKey();
                    List<Disk> disks = diskEntries.getValue().getDisks();
                    if (disks != null) {
                        for (Disk disk : disks) {
                            printMetric(metricPathPrefix, projectId + "|Disk|" + region + "|" + disk.getName() + "|SizeinGb", disk.getSizeGb());
                        }
                    }
                }
            }
        return new TaskOutput("GCEMonitor completed successfully");
    }

    private Compute createGoogleCompute(String serviceAccountId, String serviceAccountP12FilePath) {
        NetHttpTransport transport = null;
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = null;

        try {
            transport = GoogleNetHttpTransport.newTrustedTransport();
            credential = new GoogleCredential.Builder().setTransport(transport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(serviceAccountId)
                    .setServiceAccountScopes(Collections.singleton(SCOPE_READ_ONLY))
                    .setServiceAccountPrivateKeyFromP12File(new File(serviceAccountP12FilePath))
                    .build();
        } catch (GeneralSecurityException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return new Compute.Builder(
                transport, jsonFactory, null).setApplicationName(APPLICATION_NAME)
                .setHttpRequestInitializer(credential).build();
    }

    private void printMetric(String metricPrefix, String metricPath, Object metricValue) {

        MetricWriter metricWriter = super.getMetricWriter(metricPrefix + metricPath, MetricWriter.METRIC_AGGREGATION_TYPE_AVERAGE, MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
        );
        if (metricValue instanceof Double) {
            metricWriter.printMetric(String.valueOf(Math.round((Double) metricValue)));
        } else if (metricValue instanceof Float) {
            metricWriter.printMetric(String.valueOf(Math.round((Float) metricValue)));
        } else {
            metricWriter.printMetric(String.valueOf(metricValue));
        }
    }
}
