package changgou.util;

import changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FastDFSUtil {


    public static TrackerServer getTrackerServer() throws IOException {
        // 创建 TrackerClient 对象，访问 TrackerServer
        TrackerClient trackerClient = new TrackerClient();

        // 通过 TrackerClient 获取 TrackerServer 的连接对象，
        return trackerClient.getConnection();
    }


    /* 加载 Tracker 连接信息 */
    static {
        try {

            // 查找 classpath 下的文件路径
            String filename = new ClassPathResource("fdfs_client.conf").getPath();

            // 初始化 Tracker 连接信息
            ClientGlobal.init(filename);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /* 文件上传 */
    public static String[] upload(FastDFSFile file) throws IOException, MyException {

        // 附加参数
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("附加参数", "附加参数值");

        // 创建一个 Tracker 访问的客户端对象 TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        // 通过 TrackerClient 访问 TrackerServer 服务，获取连接信息（包含 Storage）
        TrackerServer trackerServer = trackerClient.getConnection();

        // 创建 StorageClient 对象，存储 Storage 的连接信息
        StorageClient storageClient = new StorageClient(trackerServer, null);

        // 通过 StorageClient 访问 Storage,实现文件上传，并且获取文件上传后的存储信息
        // 传入的参数分别是 上传文件的字节数组、扩展名、附加参数
        return storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
    }

    /* 获取文件信息
     * @param groupName 文件的组名，比如 group1
     * @param remoteFileName 文件存储路径名，比如 M00/00/00/wKjThGAyCVCASJtTAAABPGy6xXQ466.txt
     * @return
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName) throws IOException, MyException {
        // 创建 TrackerClient 对象，访问 TrackerServer
        TrackerClient trackerClient = new TrackerClient();

        // 通过 TrackerClient 获取 TrackerServer 的连接对象，
        TrackerServer trackerServer = trackerClient.getConnection();

        //通过 TrackerServer 获取到 Storage 信息，并创建 StorageClient 对象存储 Storage 信息
        StorageClient storageClient = new StorageClient(trackerServer, null);

        // 获取文件信息
        FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);
        return fileInfo;
    }


    /* 文件下载*/
    public static InputStream downLoadFile(String groupName, String remoteFileName) throws IOException, MyException {
        // 创建 TrackerClient 对象，访问 TrackerServer
        TrackerClient trackerClient = new TrackerClient();

        // 通过 TrackerClient 获取 TrackerServer 的连接对象，
        TrackerServer trackerServer = trackerClient.getConnection();

        //通过 TrackerServer 获取到 Storage 信息，并创建 StorageClient 对象存储 Storage 信息
        StorageClient storageClient = new StorageClient(trackerServer, null);
        byte[] buffer = storageClient.download_file(groupName, remoteFileName);
        return new ByteArrayInputStream(buffer);
    }


    /* 文件删除 */
    public static int deleteFile(String groupName, String remoteFileName) throws IOException, MyException {
        // 创建 TrackerClient 对象，访问 TrackerServer
        TrackerClient trackerClient = new TrackerClient();

        // 通过 TrackerClient 获取 TrackerServer 的连接对象，
        TrackerServer trackerServer = trackerClient.getConnection();

        //通过 TrackerServer 获取到 Storage 信息，并创建 StorageClient 对象存储 Storage 信息
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient.delete_file(groupName, remoteFileName);
    }


    /* 获取 Storage 信息 */
    public static StorageServer getStorage() throws IOException {
        // 创建 TrackerClient 对象，访问 TrackerServer
        TrackerClient trackerClient = new TrackerClient();

        // 通过 TrackerClient 获取 TrackerServer 的连接对象，
        TrackerServer trackerServer = trackerClient.getConnection();

        // 获取 Storage 信息
        return trackerClient.getStoreStorage(trackerServer);
    }

    /* 获取 Storage IP、端口信息 */
    public static ServerInfo[] getStorage1(String groupName, String filename) throws IOException {
        // 创建 TrackerClient 对象，访问 TrackerServer
        TrackerClient trackerClient = new TrackerClient();

        // 通过 TrackerClient 获取 TrackerServer 的连接对象，
        TrackerServer trackerServer = trackerClient.getConnection();

        return trackerClient.getFetchStorages(trackerServer, groupName, filename);
    }

    /* 获取 Tracker 信息 */
    public static String getTrackerInfo() throws IOException {
        // 创建 TrackerClient 对象，访问 TrackerServer
        TrackerClient trackerClient = new TrackerClient();

        // 通过 TrackerClient 获取 TrackerServer 的连接对象，
        TrackerServer trackerServer = trackerClient.getConnection();

        // Tracker IP、HTTP 端口
        int port = ClientGlobal.getG_tracker_http_port();
        String ip = trackerServer.getInetSocketAddress().getHostString();
        String url = "http://" + ip + ":" + port;
        return url;
    }


    public static void main(String[] args) throws IOException, MyException {
        /*
        // 获取文件信息
        FileInfo fileInfo = getFileInfo("group1", "M00/00/00/wKjThGAyCVCASJtTAAABPGy6xXQ466.txt");
        System.out.println(fileInfo.getSourceIpAddr());
        System.out.println(fileInfo.getFileSize());


        // 文件下载
        InputStream is = downLoadFile("group1", "M00/00/00/wKjThGAyCVCASJtTAAABPGy6xXQ466.txt");
        // 将文件写入本地磁盘
        FileOutputStream fos = new FileOutputStream("D:/1.txt");
        byte[] buffer = new byte[1024];
        while (is.read(buffer) != -1) {
            fos.write(buffer);
        }
        fos.flush();
        fos.close();
        fos.close();
        */

        // 删除文件
        // System.out.println(deleteFile("group1", "M00/00/00/wKjThGAySpeAar03AAABPGy6xXQ033.txt"));


        // 获取 Storage 信息

        /*
        System.out.println(getStorage());
        System.out.println(getStorage().getStorePathIndex());
        System.out.println(getStorage().getInetSocketAddress().getHostString());


        // 获取 Storage 组的 IP和端口信息
        ServerInfo[] groups = getStorage1("group1",
                "M00/00/00/wKjThGAyVUiARmICAAABPGy6xXQ596.txt");
        for (ServerInfo group : groups) {
            System.out.println(group.getIpAddr());
            System.out.println(group.getPort());
        }

         */

        System.out.println(getTrackerInfo());
    }
}

