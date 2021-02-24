package changgou.file;

import java.io.Serializable;

/* 封装文件信息 */
public class FastDFSFile implements Serializable {
    // 文件名
    private String name;

    // 文件字节数组（内容）
    private byte[] content;

    // 文件扩展名,比如 jpg、png、gif......
    private String ext;

    // 文件 MD5 摘要值，为文件生成唯一值
    private String md5;

    // 上传者
    private String author;


    public FastDFSFile(String name, byte[] content, String ext, String md5, String author) {
        this.name = name;
        this.content = content;
        this.ext = ext;
        this.md5 = md5;
        this.author = author;
    }

    public FastDFSFile(String name, byte[] content, String ext) {
        this.name = name;
        this.content = content;
        this.ext = ext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
