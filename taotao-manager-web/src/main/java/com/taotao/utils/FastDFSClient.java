package com.taotao.utils;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

/*
 * 这是一个包装过后的图片上传工具
 */
public class FastDFSClient {
	 private TrackerClient trackerClient = null;
	    private TrackerServer trackerServer = null;
	    private StorageServer storageServer = null;
	    private StorageClient1 storageClient = null;
	    
    /**	conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
	     * 这句话的意思是，如果用户传入的文件路径是相对路径(相对路径以src/main/resources目录为根目录)，
	     * 比如用户传入的文件路径是”classpath:applications.properties”，那么需要转为绝对路径，
	     * 因此需要把”classpath:”给替换掉，改为F:/Java/my-taotao/taotao-manager-web/src/main/resources。
	     * 而且封装类中使用的Storage客户端是StorageClient1而不是StorageClient。
	     * 这个客户端的好处是能够帮我们自动把文件所在的组以及存放位置拼接到一块。 
	     * @param conf
	     * @throws Exception
	     */
	    public FastDFSClient(String conf) throws Exception {
	        if (conf.contains("classpath:")) {
	            conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
	        }
	        ClientGlobal.init(conf);
	        trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();
	        storageServer = null;
	        storageClient = new StorageClient1(trackerServer, storageServer);
	    }

	    /**
	     * 上传文件方法
	     * <p>Title: uploadFile</p>
	     * <p>Description: </p>
	     * @param fileName 文件全路径
	     * @param extName 文件扩展名，不包含（.）
	     * @param metas 文件扩展信息
	     * @return
	     * @throws Exception
	     */
	    public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
	        String result = storageClient.upload_file1(fileName, extName, metas);
	        return result;
	    }

	    public String uploadFile(String fileName) throws Exception {
	        return uploadFile(fileName, null, null);
	    }

	    public String uploadFile(String fileName, String extName) throws Exception {
	        return uploadFile(fileName, extName, null);
	    }

	    /**
	     * 上传文件方法
	     * <p>Title: uploadFile</p>
	     * <p>Description: </p>
	     * @param fileContent 文件的内容，字节数组
	     * @param extName 文件扩展名
	     * @param metas 文件扩展信息
	     * @return
	     * @throws Exception
	     */
	    public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

	        String result = storageClient.upload_file1(fileContent, extName, metas);
	        return result;
	    }

	    public String uploadFile(byte[] fileContent) throws Exception {
	        return uploadFile(fileContent, null, null);
	    }

	    public String uploadFile(byte[] fileContent, String extName) throws Exception {
	        return uploadFile(fileContent, extName, null);
	    }
	    
	    
	    /**
	     * 文件上传的测试
	     */
//	    @Test
//	    public void testUpload() throws Exception {
//	        // 1.先创建一个配置文件——fast_dfs.conf，配置文件的内容就是指定TrackerServer的地址
//
//	        // 2.使用全局方法加载配置文件
//	        ClientGlobal.init("F:/Java/my-taotao/taotao-manager-web/src/main/resources/resource/fast_dfs.conf");
//	        // 3.创建一个TrackerClient对象
//	        TrackerClient trackerClient = new TrackerClient();
//	        // 4.通过TrackerClient对象获得TrackerServer对象
//	        TrackerServer trackerServer = trackerClient.getConnection();
//	        // 5.创建StorageServer的引用，null就可以了
//	        StorageServer storageServer = null;
//	        // 6.创建一个StorageClient对象，其需要两个参数，一个是TrackerServer，一个是StorageServer
//	        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
//	        // 7.使用StorageClient对象上传文件(图片)
//	        // 参数1：文件名，参数名：扩展名，不能包含"."，参数3：文件的元数据，保存文件的原始名、大小、尺寸等，如果没有可为null
//	        String[] strings = storageClient.upload_file("F:/fastdfs_test/meinv.jpg", "jpg", null);
//	        for (String string : strings) {
//	            System.out.println(string);
//	        }
//	    }
}
