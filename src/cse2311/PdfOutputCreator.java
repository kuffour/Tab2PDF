

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfOutputCreator {
	float size; 

	public static void makePDF(MusicSheet ms,Style s) throws IOException,DocumentException {
		
		Document document = new Document(PageSize.LETTER);
		PdfWriter write = PdfWriter.getInstance(document, new FileOutputStream(
				new File(ms.get_Title() + ".pdf")));
		document.open();
		write.open();
		PdfContentByte draw = write.getDirectContent();

		printTitle(ms.get_Title(), ms.get_Subtitle(), document);


		// example of adding 0+1, 2+3, 4+5 etc
		// page width 612 points
		// page height 792 points

		// first line starts at x point 36
		// currY = current y
		float currX = 0.0f;
		float currY = 680.0f;
		float lastWordX = currX;//location of last printed num/letter for arc
		float lastWordY = currY;
		
		// drawLine(0,currY, 36, currY, draw)
		// private static void DrawLine(float x, float y, float toX, float toY,
		// PdfContentByte draw)
		// lineTo(float x, float y)
		
		for (Staff staff : ms.get_Staffs()) {
			int j = 0;
			for (StringBuffer sLine : staff.get_Lines()) {
				/*
				 * String add = ms.data.get(i).get(j) + ms.data.get(i+1).get(j);
				 * Paragraph line_x = new Paragraph(add);
				 * line_x.setAlignment(Element.ALIGN_LEFT);
				 * document.add(line_x);
				 */
				
				String line = sLine.toString();
				drawHorLine(currX, currY, 36.0f, draw); //drawn from beginning of page to starting bar (calvin)
				currX += 36.0f;
			
				for (int z = 0; z < line.length(); z++) {
					char l = line.charAt(z);
					char m = 99;
					if(z <line.length()-1)
						 m = line.charAt(z+1);
					if (l == '-') {
						drawHorLine(currX, currY, 5.02f, draw);
						currX = currX + 5.02f;
					} else if (l == '|') {
						
						if(j < 5) //this was done to not add an extra vertical line
							drawVerLine(currX, currY-3.5f, 7.0f, draw); 
						// drawHorLine(currX, currY, 1.8f, draw);
						// drawVerLine(currX, currY, 7.0f, draw);
					} else if (l == '*'){
						drawCircle(currX, currY, draw);
						drawHorLine(currX, currY, 5.02f, draw);
						currX = currX + 5.02f;
					} else if (l == '/'){
						drawHorLine(currX, currY, 5.02f, draw);
						//lastWordX = currX; //set location of last num/letter
						//lastWordY = currY;
						drawDiagonal (currX+2f, currY+3.25f, draw); //aligned the text with horizontal lines (calvin)
						currX = currX + 5.02f;
					} else if (l == '>') {
							drawDiamond(currX + 3f, currY + 3.5f, draw);
							currX = currX + 5.02f;
					}else if (l == ',') {
						drawHorLine(currX, currY, 1f, draw);
						currX = currX + 1f;
					}else if (l == 'p'&& z < (line.length() - 1)
							&& line.charAt(z - 1) == '|'){
							createBezierCurves(draw,lastWordX+2,lastWordY-3,currX+7.02f,currY+13);
							drawHorLine(currX, currY, 5.02f, draw);
							text(l + "", currX - 1.0f, currY + 10.0f,s.my_Fontface,4,draw);
							currX = currX + 5.02f;
							 //aligned the text with horizontal lines (calvin)
							
					} else if (l == 'D') {
						if(j < 5)
							drawThick(currX, currY-4f, 5.02f, draw);
						
					}else if (l == '(') {
						System.out.println("Repeat "+staff.getrepeatNum() +" Times");
						
					} else {
						lastWordX = currX; //set location of last num/letter
						lastWordY = currY;
						text(l + "", currX, currY+1.25f,s.my_Fontface, 8, draw);
						if((l>47&&l<58)&&(m>47&&m<58)){
							currX = currX + 3.5f;
							text(m + "", currX, currY+1.25f,s.my_Fontface, 8, draw);
							currX = currX + 1.52f;
							
							z++;
							currX = currX + 5.02f;
							
						}else{
						 //aligned the text with horizontal lines (calvin)
							currX = currX + 5.02f;
						}
					}
				}
				drawHorLine(currX, currY, 612.0f - currX, draw); //drawn from last bar to end of page (calvin)
				currX = 0.0f;
				j++;
				currY = currY - 7.0f;
			}
			currX = 0.0f;
			currY = currY - 15.0f;
			if (currY <= 10) {
				document.newPage();
				currY = 750;
			}
			// document.add(new Paragraph("\n"));
		}

		document.close();
		write.close();

	}

	private static void printTitle(String title, String subtitle,
			Document document) throws DocumentException {
		Font[] fonts = {
				new Font(),

				/* new Font(fontfamily, size, type, color) */
				new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD,
						new BaseColor(/* Red */0, /* Green */0, /* Blue */0)),
				new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL,
						new BaseColor(/* Red */0, /* Green */0, /* Blue */0)), };


		Paragraph Title = new Paragraph(title, fonts[1]);
		Title.setAlignment(1);

		document.add(Title);
		Paragraph subTitle = new Paragraph(subtitle, fonts[2]);

		subTitle.setAlignment(1);
		document.add(subTitle);
	}

	private static void drawHorLine(float currX, float currY, float toX,
			PdfContentByte draw) {
		draw.setLineWidth(.5f);
		draw.moveTo(currX, currY + 3.3f);
		draw.lineTo(currX + toX, currY + 3.3f);
		draw.stroke();
	}

	private static void drawVerLine(float currX, float currY, float toY,
			PdfContentByte draw) {
		draw.setLineWidth(.5f);
		draw.moveTo(currX, currY);
		draw.lineTo(currX, currY + toY);
		draw.stroke();
	}

	private static void drawDiamond(float currX, float currY, PdfContentByte draw) {
		currX = currX - 1.75f;
		currY = currY + 1.75f;
		draw.moveTo(currX + 0.175f, currY + 0.175f);
		draw.lineTo(currX - 1.93f, currY - 1.93f);
		draw.stroke();
		draw.moveTo(currX, currY);
		draw.lineTo(currX + 1.93f, currY - 1.93f);
		draw.stroke();
		currY = currY - (3.5f);
		draw.moveTo(currX - 0.175f, currY - 0.175f);
		draw.lineTo(currX + 1.93f, currY + 1.93f);
		draw.stroke();
		draw.moveTo(currX, currY);
		draw.lineTo(currX - 1.93f, currY + 1.93f);
		draw.stroke();
	}

	private static void drawCircle(float currX, float currY, PdfContentByte draw) {
		draw.circle(currX+2.3f, currY+3.3f, 1.5f);
		draw.setColorFill(BaseColor.BLACK);
		draw.fillStroke();
	}

	private static void drawThick(float currX, float currY, float toY,
			PdfContentByte draw) {
		draw.setLineWidth(1.8f);
		draw.moveTo(currX, currY);
		draw.lineTo(currX, currY + toY + 2.5f);
		draw.stroke();
	}

	private static void drawDiagonal(float currX, float currY,
			PdfContentByte draw) {
		draw.moveTo(currX, currY);
		draw.lineTo(currX + 1.75f, currY + 1.75f);
		draw.stroke();
		draw.moveTo(currX, currY);
		draw.lineTo(currX - 1.75f, currY - 1.75f);
		draw.stroke();
	}

	private static void text(String text, float currX, float currY,
			BaseFont font,int fontsize, PdfContentByte draw) throws DocumentException,
			IOException {
	
		draw.saveState();
		draw.beginText();
		draw.setTextMatrix(currX, currY);
		draw.setFontAndSize(font, fontsize);
		draw.showText(text);
		draw.endText();
		draw.restoreState();
	}

	
	private static void createBezierCurves(PdfContentByte cb, float x0, float y0,
		         float x3, float y3) {        
		cb.arc(x0 - 6.0F, y0 - 8.5F, x3 + 6.5F, y3 - 4.5F, 60, 60);
    }
}
