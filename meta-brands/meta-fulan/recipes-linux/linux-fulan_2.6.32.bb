SUMMARY = "Linux kernel for ${MACHINE}"
LICENSE = "GPLv2"
SECTION = "kernel"
PACKAGE_ARCH = "${MACHINE_ARCH}"

KV = "2.6.32"
SRCDATE = "20160701"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

MACHINE_KERNEL_PR:append = ".9"

inherit kernel machine_kernel_pr machinstall

DEPENDS:append:spark7162 = " \
  stlinux24-sh4-stx7105-fdma-firmware \
"

DEPENDS:append:spark = " \
  stlinux24-sh4-stx7111-fdma-firmware \
"

# By default, kernel.bbclass modifies package names to allow multiple kernels
# to be installed in parallel. We revert this change and rprovide the versioned
# package names instead, to allow only one kernel to be installed.
PKG:${KERNEL_PACKAGE_NAME}-base = "kernel-base"
PKG:${KERNEL_PACKAGE_NAME}-image = "kernel-image"
RPROVIDES:${KERNEL_PACKAGE_NAME}-base = "kernel-${KERNEL_VERSION}"
RPROVIDES:kernel-image = "kernel-image-${KERNEL_VERSION}"

STM_PATCH_STR = "0217"
LINUX_VERSION = "2.6.32.71"
SRCREV = "3ec500f4212f9e4b4d2537c8be5ea32ebf68c43b"

SRC_URI = "git://github.com/Duckbox-Developers/linux-sh4-2.6.32.71.git;protocol=https;branch=stmicro \
    file://linux-kbuild-generate-modules-builtin_stm24_${STM_PATCH_STR}.patch \
    file://linux-sh4-linuxdvb_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-linuxdvb_stm24_${STM_PATCH_STR}_ca.patch;patch=1 \
    file://linux-sh4-sound_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-time_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-init_mm_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-copro_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-strcpy_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-squashfs-lzma_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-ext23_as_ext4_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-bpa2_procfs_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-ftdi_sio.c_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-lzma-fix_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-tune_stm24.patch;patch=1 \
    file://linux-sh4-permit_gcc_command_line_sections_stm24.patch;patch=1 \
    file://linux-sh4-mmap_stm24.patch;patch=1 \
    file://linux-ratelimit-bug_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-patch_swap_notify_core_support_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-cpuinfo.patch;patch=1 \
    file://linux-sh4-add_missing_eid.patch;patch=1 \
    file://silence_conv_i2sspdif_warning.patch;patch=1 \
    file://linux-sh4-linux_yaffs2.patch;patch=1 \
    file://linux-sh4-fix-crash-usb-reboot.patch;patch=1 \
    file://timeconst_perl5.patch;patch=1 \
    file://linux-sh4-stmmac_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-lmb_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://defconfig \
    file://st-coprocessor.h \
    file://linux-net_stm24.patch;patch=1 \
    file://taskstats.patch;patch=1 \
"

SRC_URI:append:spark7162 = " \
    file://linux-sh4-spark7162_setup_stm24_${STM_PATCH_STR}.patch;patch=1 \
"

SRC_URI:append:spark = " \
    file://linux-sh4-spark_setup_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-lirc_stm_stm24_${STM_PATCH_STR}.patch;patch=1 \
    file://linux-sh4-spark-af901x-NXP-TDA18218.patch;patch=1 \
    file://linux-sh4-spark-dvb-as102.patch;patch=1 \
"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

export OS = "Linux"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_IMAGETYPE = "uImage"
KERNEL_IMAGEDEST = "tmp"
KEEPUIMAGE = "yes"
PARALLEL_MAKEINST = ""

# bitbake.conf only prepends PARALLEL make in tasks called do_compile, which isn't the case for compile_modules
# So explicitly enable it for that in here
EXTRA_OEMAKE:prepend = " ${PARALLEL_MAKE} "

PACKAGES =+ "kernel-headers"
FILES:kernel-headers = "${exec_prefix}/src/linux*"
FILES:${KERNEL_PACKAGE_NAME}-dev += "${includedir}/linux"
FILES:${KERNEL_PACKAGE_NAME}-image = "/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}"

KERNEL_CONFIG_COMMAND = "oe_runmake -C ${S} O=${B} oldconfig"

do_configure:prepend() {
    oe_machinstall -m 0644 ${WORKDIR}/defconfig ${B}/.config
    sed -i "s#^\(CONFIG_EXTRA_FIRMWARE_DIR=\).*#\1\"${STAGING_DIR_HOST}/lib/firmware\"#" ${B}/.config;
}

do_shared_workdir:prepend() {
    # Workaround for missing dir required in latest oe
    mkdir -p ${B}/include/generated
    touch ${B}/include/generated/null
}

do_shared_workdir:append() {
    kerneldir=${STAGING_KERNEL_BUILDDIR}
    if [ -f include/linux/bounds.h ]; then
        mkdir -p $kerneldir/include/linux
        cp -f include/linux/bounds.h $kerneldir/include/linux/bounds.h
    fi
    if [ -f include/asm-sh/machtypes.h ]; then
        mkdir -p $kerneldir/include/asm-sh
        # ln -sf $kerneldir/include/asm-sh $kerneldir/include/asm
        cp -f include/asm-sh/machtypes.h $kerneldir/include/asm-sh
    fi
    if [ -e include/linux/utsrelease.h ]; then
        mkdir -p $kerneldir/include/linux
        cp -f include/linux/utsrelease.h $kerneldir/include/linux/utsrelease.h
    fi
}

do_install:append() {
    install -d ${D}${includedir}/linux	
    install -m 644 ${WORKDIR}/st-coprocessor.h ${D}${includedir}/linux
    oe_runmake headers_install INSTALL_HDR_PATH=${D}${exec_prefix}/src/linux-${KERNEL_VERSION} ARCH=$ARCH
    mv ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION} ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}
}

# hack to override kernel.bbclass...
# uimages are already built in kernel compile
do_uboot_mkimage() {
    :
}

pkg_postinst:kernel-image() {
    if [ "x$D" == "x" ]; then
        if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE} ] ; then
            if grep -q root=/dev/mtdblock6 /proc/cmdline; then
                flash_eraseall /dev/${MTD_KERNEL}
            nandwrite -p /dev/${MTD_KERNEL} /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}
            rm -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}
            else
                flash_erase /dev/${MTD_KERNEL} 0x400000 0x20
                nandwrite -s 0x400000 -p /dev/${MTD_KERNEL} /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}
            fi
        fi
    fi
    true
}

do_rm_work() {
}

# extra tasks
addtask kernel_link_images after do_compile before do_install
