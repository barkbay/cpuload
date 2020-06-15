import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

public class CpuLoad {

    public static void main(String[] args) {
        OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        String cgroup = findCgroup();
        System.out.println("cgroup: " + cgroup);
        int threadCount = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        boolean firstTime = true;

        while (true) {
            System.out.println(osBean.getCpuLoad());
            dumpCpuStat(cgroup);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!firstTime && threadCount-- > 0) {
                startThread();
            }
            firstTime = false;
        }
    }

    private static void dumpCpuStat(String cgroup) {
        try {
            List<String> strings = Files.readAllLines(FileSystems.getDefault().getPath("/sys/fs/cgroup/cpu/" + cgroup, "cpu.stat"));
            strings.forEach(s -> System.out.println("  " + s));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String findCgroup() {
        try {
            List<String> strings = Files.readAllLines(FileSystems.getDefault().getPath("/proc/self/cgroup"));
            return strings.stream().filter(s -> s.contains("cpuacct")).map(s -> s.split(":")[2]).findFirst().get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void startThread() {
        System.out.println("Starting thread");
        new Thread(() -> {
            long now = System.currentTimeMillis();
            while (System.currentTimeMillis() < now + 20000) {
            }
            System.out.println("Done in thread");
        }).start();
    }
}
