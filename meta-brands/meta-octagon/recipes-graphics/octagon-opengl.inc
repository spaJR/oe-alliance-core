SUMMARY = "libgles for ${SOC_FAMILY}"
SECTION = "base"
PRIORITY = "required"
LICENSE = "CLOSED"
PACKAGE_ARCH := "${MACHINE_ARCH}"

PR = "r1-${SRCDATE}"

PROVIDES = "virtual/libgles1 virtual/libgles2 virtual/egl"
RPROVIDES:${PN} = "virtual/libgles1 virtual/libgles2 virtual/egl libEGL.so libGLESv2.so libmali.so"

DEPENDS = "patchelf-native"

SRC_URI = "https://source.mynonpublic.com/octagon/${SOC_FAMILY}-opengl-${SRCDATE}.tar.gz"

S = "${WORKDIR}/usr"

do_configure() {
}

do_compile() {
}

do_install() {
    install -d ${D}${libdir}
    install -m 0755 ${S}/lib/libMali.so ${D}${libdir}/
    patchelf --set-soname libMali.so ${D}${libdir}/libMali.so
    ln -s libMali.so ${D}${libdir}/libGLESv2.so.2.0
    ln -s libGLESv2.so.2.0 ${D}${libdir}/libGLESv2.so.2
    ln -s libGLESv2.so.2 ${D}${libdir}/libGLESv2.so
    ln -s libMali.so ${D}${libdir}/libGLESv1_CM.so.1.1
    ln -s libGLESv1_CM.so.1.1 ${D}${libdir}/libGLESv1_CM.so.1
    ln -s libGLESv1_CM.so.1 ${D}${libdir}/libGLESv1_CM.so
    ln -s libMali.so ${D}${libdir}/libEGL.so.1.4
    ln -s libEGL.so.1.4 ${D}${libdir}/libEGL.so.1
    ln -s libEGL.so.1 ${D}${libdir}/libEGL.so
    install -d ${D}/${includedir}
    install -m 0644 ${S}/include/hi_dbe.h ${D}${includedir}/
    for d in EGL GLES GLES2 KHR; do
        install -d ${D}${includedir}/$d
        for f in ${S}/include/$d/*.h; do
           install -m 0644 $f ${D}${includedir}/$d/
        done
    done
}

FILES:${PN} = "/usr/lib/*"
FILES:${PN}-dev = "/usr/include/*"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "already-stripped dev-so ldflags"
