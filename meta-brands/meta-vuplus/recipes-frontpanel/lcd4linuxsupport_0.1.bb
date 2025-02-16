SUMMARY = "lcd4linux support for duo2"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

PACKAGE_ARCH = "${MACHINE_ARCH}"
RDEPENDS:${PN} = "enigma2 png-util"

SRC_URI = "file://plugin.py"

S = "${WORKDIR}"

PR = "r9"

PLUGINPATH = "/usr/lib/enigma2/python/Plugins/Extensions/LCD4linuxSupport"

do_install() {
    install -d  ${D}${PLUGINPATH}
    install -m 0600 ${S}/plugin.py ${D}${PLUGINPATH}
    touch ${D}${PLUGINPATH}/__init__.py
}

FILES:${PN} = "${PLUGINPATH}/*.pyo"

