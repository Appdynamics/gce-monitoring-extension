/*
 * Copyright 2014. AppDynamics LLC and its affiliates.
 *  All Rights Reserved.
 *  This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *  The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.monitors.gce;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.DiskAggregatedList;
import com.google.api.services.compute.model.DisksScopedList;
import com.google.api.services.compute.model.Project;
import com.google.api.services.compute.model.Quota;
import com.google.api.services.compute.model.Region;
import com.google.api.services.compute.model.RegionList;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class GCEStats {

    private static final Logger LOG = Logger.getLogger(GCEStats.class);

    public static List<Quota> getProjectStats(Compute compute, String projectId) {
        Compute.Projects projects = compute.projects();

        try {
            Compute.Projects.Get get = projects.get(projectId);
            Project project = get.execute();
            return project.getQuotas();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static List<Region> getRegionStats(Compute compute, String projectId) {
        Compute.Regions regions = compute.regions();

        try {
            Compute.Regions.List list = regions.list(projectId);
            RegionList regionList = list.execute();
            return regionList.getItems();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, DisksScopedList> getDiskStats(Compute compute, String projectId) {
        Compute.Disks disks = compute.disks();

        try {
            Compute.Disks.AggregatedList aggregatedList = disks.aggregatedList(projectId);
            DiskAggregatedList diskAggregatedList = aggregatedList.execute();
            return diskAggregatedList.getItems();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
