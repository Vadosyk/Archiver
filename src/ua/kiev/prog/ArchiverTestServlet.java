package ua.kiev.prog;

        import org.apache.commons.fileupload.FileItemIterator;
        import org.apache.commons.fileupload.FileItemStream;
        import org.apache.commons.fileupload.FileUploadException;
        import org.apache.commons.fileupload.servlet.ServletFileUpload;

        import javax.servlet.ServletOutputStream;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import java.io.IOException;
        import java.io.InputStream;
        import java.util.zip.ZipEntry;
        import java.util.zip.ZipOutputStream;

public class ArchiverTestServlet extends HttpServlet {

    String filename;
    String archiveName;
    public void parseFileName(String filename) {
        int lastSlash = filename.lastIndexOf("\\");
        if (lastSlash > 0)
            filename = filename.substring(lastSlash);
        int lastPoint = filename.lastIndexOf(".");
        if (lastPoint > 0)
            archiveName = filename.substring(0, lastPoint);
        else
            archiveName = filename;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {

        ServletOutputStream servletOutputStream = resp.getOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream((servletOutputStream));

        ServletFileUpload upload = new ServletFileUpload();
        try {
            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                if (!item.isFormField()) {
                    filename = item.getName();
                    InputStream inputStream = item.openStream();

                    parseFileName(filename);

                    resp.setContentType("application/zip");
                    resp.setHeader("Content-Disposition", "inline; filename=" + archiveName + ".zip;");

                    ZipEntry zipEntry = new ZipEntry(filename);
                    zipOutputStream.putNextEntry(zipEntry);

                    int len;
                    byte[] buffer = new byte[8192];
                    while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                    zipOutputStream.closeEntry();
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        zipOutputStream.close();
        servletOutputStream.flush();
        servletOutputStream.close();

    }
}
