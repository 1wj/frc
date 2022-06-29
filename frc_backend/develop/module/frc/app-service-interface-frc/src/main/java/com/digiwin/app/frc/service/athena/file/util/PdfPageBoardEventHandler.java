package com.digiwin.app.frc.service.athena.file.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.IOException;

/**
 * 给pdf添加边框等-pdf页面事件处理
 *
 * @author Jiangzhou
 * @date 2022/04/27
 */
public class PdfPageBoardEventHandler extends PdfPageEventHelper {
    public float offset = 5;
    public float startPosition;
    public float tableHight;
    public int pageNum = 0;

    public float x = 0;
    public float fristPagePosition = 0;

    public int lastPageNum = 0;

    /**
     * 设置pdf页数
     *
     * @param lastPageNum
     */
    public void setLastPageNum(int lastPageNum) {
        this.lastPageNum = lastPageNum;
    }

    public boolean active = false;

    /**
     * 关闭pdf页面监听事件
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
        this.startPosition = paragraphPosition;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        startPosition = document.top();
    }

    /**
     * 设置左、上框与正文的间隔
     *
     * @param x
     */
    public void setPosition(float x) {
        this.x = x;
    }

    /**
     * 设置首页与框与上方距离
     *
     * @param fristPagePosition
     */
    public void setFristPagePosition(float fristPagePosition) {
        this.fristPagePosition = fristPagePosition;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT,
                    new Phrase("鼎捷软件", PDFUtil.getFont(Font.NORMAL)), document.left(), document.top() + 20, 0);
            LineSeparator lineSeparator =
                    new LineSeparator(0f, 110, BaseColor.BLACK, Element.ALIGN_LEFT, 0);
            lineSeparator.drawLine(writer.getDirectContent(), document.left() - x,
                    document.right() - document.left() + x, document.top() + 15);

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        pageNum++;
        if (pageNum == lastPageNum) {
            return;
        }
        if (active) {
            PdfContentByte cb = writer.getDirectContentUnder();
            cb.setColorStroke(BaseColor.BLACK);
            if (pageNum == 1) {
                cb.rectangle(document.left() - x, document.bottom(), document.right() - document.left() + x,
                        document.top() - document.bottom() - fristPagePosition);
            } else {
                cb.rectangle(document.left() - x, document.bottom(), document.right() - document.left() + x,
                        document.top() - document.bottom());
            }
            cb.stroke();
        }
    }

    @Override
    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
        if (active) {
            PdfContentByte cb = writer.getDirectContent();
            cb.setColorStroke(BaseColor.BLACK);
            cb.rectangle(document.left() - x, paragraphPosition - offset, document.right() - document.left() + x,
                    startPosition - paragraphPosition + x);
            // cb.rectangle(x, y, w, h);

            cb.stroke();
        }
    }


}
