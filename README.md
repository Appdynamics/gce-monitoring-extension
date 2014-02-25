# Google Computing Engine Monitoring Extension

This extension works only with the standalone machine agent.

##Use Case

With GCE we can run large-scale workloads on virtual machines hosted on Google's infrastructure. The GCE monitoring extension captures statistics from the GCE and displays them in the AppDynamics Metric Browser.

##Prerequisite
Create a service account for your GCE project and download the privatekey file (.p12 file).

##Installation

1. Run "mvn clean install"
2. Download and unzip the file 'target/GCEMonitor.zip' to \<machineagent install dir\}/monitors
3. Open <b>monitor.xml</b> and configure the GCE arguments

<pre>
&lt;argument name="ServiceAccountId" is-required="true" default-value="556793072....@developer.gserviceaccount.com"  /&gt;
&lt;argument name="ServiceAccountP12FilePath" is-required="true" default-value="/home/.../GCE/privatekey.p12" /&gt;
&lt;argument name="ProjectID" is-required="true" default-value="appdynamics-gce" /&gt;
</pre>

<b>ServiceAccountId</b> : GCE Project service account id <br/>
<b>ServiceAccountP12FilePath</b> : GCE Project service account private key <br/>
<b>ProjectID</b> : GCE Project ID <br/>


##Metrics
The following metrics are reported.

###Disk

| Metric Path  | Description  |
|---------------- |------------- |
|GCE/{Project ID}/Disk/{Zone}/{Disk Name}/SizeinGb| Size of the Disk in Gb|

###Quotas

| Metric Path  | Description  |
|---------------- |------------- |
|GCE/{Project ID}/Quota/FIREWALLS/Limit| Number of fire walls allowed|
|GCE/{Project ID}/Quota/FIREWALLS/Usage| Number of fire walls used|
|GCE/{Project ID}/Quota/FORWARDING_RULES/Limit| Number of forwarding rules allowed|
|GCE/{Project ID}/Quota/FORWARDING_RULES/Usage| Number of forwarding rules used|
|GCE/{Project ID}/Quota/HEALTH_CHECKS/Limit| Number of health checks allowed|
|GCE/{Project ID}/Quota/HEALTH_CHECKS/Usage| Number of health checks used|
|GCE/{Project ID}/Quota/IMAGES/Limit| Number of images allowed|
|GCE/{Project ID}/Quota/IMAGES/Usage| Number of images used|
|GCE/{Project ID}/Quota/NETWORKS/Limit| Number of networks allowed|
|GCE/{Project ID}/Quota/NETWORKS/Usage| Number of networks used|
|GCE/{Project ID}/Quota/ROUTES/Limit| Number of routes allowed|
|GCE/{Project ID}/Quota/ROUTES/Usage| Number of routes used|
|GCE/{Project ID}/Quota/SNAPSHOTS/Limit| Number of snapshots allowed|
|GCE/{Project ID}/Quota/SNAPSHOTS/Usage| Number of snapshots used|
|GCE/{Project ID}/Quota/TARGET_INSTANCES/Limit| Number of target instances allowed|
|GCE/{Project ID}/Quota/TARGET_INSTANCES/Usage| Number of target instances used|
|GCE/{Project ID}/Quota/TARGET_POOLS/Limit| Number of target pools allowed|
|GCE/{Project ID}/Quota/TARGET_POOLS/Usage| Number of target pools used|


###Region
| Metric Path  | Description  |
|---------------- |------------- |
|GCE/{Project ID}/Region/{region}/Quota/CPUS/Limit|Number of CPUs allowed|
|GCE/{Project ID}/Region/{region}/Quota/CPUS/Usage|Number of CPUs used|
|GCE/{Project ID}/Region/{region}/Quota/DISKS_TOTAL_GB/Limit|Disk size allowed|
|GCE/{Project ID}/Region/{region}/Quota/DISKS_TOTAL_GB/Usage|Disk size used|
|GCE/{Project ID}/Region/{region}/Quota/IN_USE_ADDRESSES/Limit|in-use addresses allowed|
|GCE/{Project ID}/Region/{region}/Quota/IN_USE_ADDRESSES/Usage|in-use addresses used|
|GCE/{Project ID}/Region/{region}/Quota/STATIC_ADDRESSES/Limit|statis addresses allowed|
|GCE/{Project ID}/Region/{region}/Quota/STATIC_ADDRESSES/Usage|statis addresses used|

#Custom Dashboard
![]()

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere]() community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).
