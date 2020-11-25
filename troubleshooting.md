
# Troubleshooting

### A list of common issues and solutions for spring-boot-bitcoin-starter

#### 1) While building, I am getting something like "OCI runtime create failed"...
Are you using a distribution with cgroup v2? (e.g. Fedora switched to cgroups v2 in v31 - "Docker no longer works").
You can temporarily switch back to using cgroup v1: 
```shell
sudo grubby --update-kernel=ALL --args="systemd.unified_cgroup_hierarchy=0"
reboot
```
In case you want to undo this change this later.
This re-enables the usage of cgroup v2 (unified cgroup hierarchy):
```shell
sudo grubby --update-kernel=ALL --args="systemd.unified_cgroup_hierarchy"
```
For more information [see this article on how to configure Docker on Fedora](https://www.linuxuprising.com/2019/11/how-to-install-and-use-docker-on-fedora.html)
