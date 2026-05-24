package com.nadia.caslab.lwjgl3;

import org.lwjgl.system.macosx.LibC;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class StartupHelper {

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    public static boolean startNewJvmIfRequired() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("mac")) return false;
        if (System.getProperty(JVM_RESTARTED_ARG) != null) return false;

        long pid = LibC.getpid();
        try {
            String javaExec = ProcessHandle.current().info().command()
                .orElse(System.getProperty("java.home") + "/bin/java");

            ProcessBuilder builder = new ProcessBuilder();
            builder.command().add(javaExec);
            for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                if (!arg.contains("-agentlib")) builder.command().add(arg);
            }
            builder.command().add("-D" + JVM_RESTARTED_ARG + "=1");
            builder.command().add("-XstartOnFirstThread");
            builder.command().add("-cp");
            builder.command().add(ManagementFactory.getRuntimeMXBean().getClassPath());
            builder.command().add(Lwjgl3Launcher.class.getName());

            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) System.out.println(line);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
