import cn.hutool.core.img.BackgroundRemoval;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WSAT
 * @Description:
 * @date 2022/7/2614:30
 */
public class Main {

    public static String inFilePath="e:/锦上添花期末大赢家七年级上册英语湘教版新题型.pdf";
    public static String outFilePath=inFilePath.replace(".pdf","")+"去水印.pdf";
    /**
     * 需要去掉的水印颜色色值
     */
    public static String[] colorRgb=new String[]{"#E9F7F0","#F5FCF0"};
    public static List<PDRectangle> pdRectangles=new ArrayList<PDRectangle>();
    public static void main(String[] args) throws IOException {
        List<BufferedImage> imgs = pdf2BufferedImage(inFilePath,1.8f);
        removeLogoColor(imgs);
        img2Pdf(imgs);
    }

    public static void img2Pdf(List<BufferedImage> imgs) throws IOException {
        PDDocument outDocument = new PDDocument();
        int page=0;
        for(BufferedImage img:imgs){
            PDImageXObject imageXObject = LosslessFactory.createFromImage(outDocument, img);
             PDPage pdPage = new PDPage(pdRectangles.get(page));
            outDocument.addPage(pdPage);
            PDPageContentStream pageContentStream = new PDPageContentStream(outDocument, pdPage);
            float height = pdPage.getMediaBox().getHeight();
            float y = pdPage.getMediaBox().getHeight() - height;
            pageContentStream.drawImage(imageXObject, 0, y, pdPage.getMediaBox().getWidth(), height);
            pageContentStream.close();
        }
        File outFile = new File(outFilePath);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        outDocument.save(outFile);
        outDocument.close();
    }
    public static List<BufferedImage> pdf2BufferedImage(String filepath) throws IOException {
        return  pdf2BufferedImage(filepath,1.0F);
    }
    public static List<BufferedImage> pdf2BufferedImage(String filepath, float scale) throws IOException {
        File file = new File(filepath);
        PDDocument document = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(document);
        int numberOfPages = document.getNumberOfPages();
        List<BufferedImage> imgs=new ArrayList<BufferedImage>();
        for(int i=0;i<numberOfPages;i++){
            pdRectangles.add(document.getPage(i).getMediaBox());
            imgs.add(renderer.renderImage(i,scale));
        }
        document.close();
        return imgs;
    }

    public static void removeLogoColor(List<BufferedImage> imgs){
        for(BufferedImage img:imgs){
            Graphics2D graphics = img.createGraphics();
            int width = img.getWidth();
            int height = img.getHeight();
            for(int w=0;w<width;w++){
                for(int h=0;h<height;h++){
                    if(likeLogoColor(img.getRGB(w, h))){
                        img.setRGB(w,h,Color.WHITE.getRGB());
                    }
                }
            }
        }
    }

    public static boolean likeLogoColor(int rgb){
        Color oriColor = new Color(rgb);
        for(int i=0;i<colorRgb.length;i++){
            Color logoColor=Color.decode(colorRgb[i]);
            if(BackgroundRemoval.areColorsWithinTolerance(oriColor,logoColor,10)){
                return true;
            }
        }
        return false;

    }
}
