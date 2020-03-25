package com.alisson.springboot.datajpa.app.view.pdf;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.alisson.springboot.datajpa.app.model.entity.Factura;
import com.alisson.springboot.datajpa.app.model.entity.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {
	
	@Autowired
    private MessageSource messageSource;
	
	@Autowired
    private LocaleResolver localeResolver;
	

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//primera forma de acceso a mensajes
		Locale locale = localeResolver.resolveLocale(request);
		//sengunda forma de acceso a mensajes
		MessageSourceAccessor mensaje = getMessageSourceAccessor();
		 
		Factura factura =(Factura)model.get("factura");
		
		PdfPTable tabla = new PdfPTable(1);
		tabla.setSpacingAfter(20);
		
		PdfPCell  cell=null;
		
		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.cliente", null, locale)));
		cell.setBackgroundColor(new Color(184,218,255));
		cell.setPadding(8f);
		cell.setBorderColor(new Color(184,218,255));
		
		tabla.addCell(cell);
		
		cell = new PdfPCell(new Phrase(factura.getCliente().getNombre()+" "+factura.getCliente().getApellido()));
		cell.setPadding(5f);
		cell.setBorderColor(new Color(224,224,224));
		
		tabla.addCell(cell);
		
		cell = new PdfPCell(new Phrase(factura.getCliente().getEmail()));
		cell.setPadding(5f);
		cell.setBorderColor(new Color(224,224,224));
		
		tabla.addCell(cell);

		PdfPTable tabla2 = new PdfPTable(1);
		tabla2.setSpacingAfter(20);
		
		cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.factura",null,locale)));
		cell.setBackgroundColor(new Color(195,230,203));
		cell.setPadding(8f);
		cell.setBorderColor(new Color(195,230,203));
		tabla2.addCell(cell);
		
		cell = new PdfPCell(new Phrase(mensaje.getMessage("text.cliente.factura.folio")+": " + factura.getId()));
		cell.setPadding(5f);
		cell.setBorderColor(new Color(224,224,224));
		tabla2.addCell(cell);
		
		cell = new PdfPCell(new Phrase(mensaje.getMessage("text.cliente.factura.descripcion")+": "+factura.getDescripcion()));
		cell.setPadding(5f);
		cell.setBorderColor(new Color(224,224,224));
		tabla2.addCell(cell);
		
		cell = new PdfPCell(new Phrase(mensaje.getMessage("text.cliente.factura.fecha")+": "+factura.getCreateAt()));
		cell.setPadding(5f);
		cell.setBorderColor(new Color(224,224,224));
		tabla2.addCell(cell);
		
		document.add(tabla);
		document.add(tabla2);
		
		PdfPTable tabla3 = new PdfPTable(4);
		tabla3.setWidths(new float[] {3.5f,1,1,1});
		
		
		cell = new PdfPCell(new Phrase(mensaje.getMessage("text.factura.form.item.nombre")));
		cell.setBackgroundColor(new Color(214, 214, 214));
		cell.setBorderColor(new Color(214, 214, 214));
		cell.setPadding(8f);
		tabla3.addCell(cell);
		

		cell = new PdfPCell(new Phrase(mensaje.getMessage("text.factura.form.item.precio")));
		cell.setBackgroundColor(new Color(214, 214, 214));
		cell.setBorderColor(new Color(214, 214, 214));
		cell.setPadding(8f);
		tabla3.addCell(cell);
		
		cell = new PdfPCell(new Phrase(mensaje.getMessage("text.factura.form.item.cantidad")));
		cell.setBackgroundColor(new Color(214, 214, 214));
		cell.setBorderColor(new Color(214, 214, 214));
		cell.setPadding(8f);
		tabla3.addCell(cell);
		
		cell = new PdfPCell(new Phrase(mensaje.getMessage("text.factura.form.item.total")));
		cell.setBackgroundColor(new Color(214, 214, 214));
		cell.setBorderColor(new Color(214, 214, 214));
		cell.setPadding(8f);
		tabla3.addCell(cell);
		
		for(ItemFactura item: factura.getItems()) {
			tabla3.addCell(item.getProducto().getNombre());
			tabla3.addCell(item.getProducto().getPrecio().toString());
			
			cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			
			tabla3.addCell(cell);
			tabla3.addCell(item.calcularImporte().toString());
			
		}
		
		cell= new PdfPCell(new Phrase(mensaje.getMessage("text.factura.form.total")+": "));
		cell.setColspan(3);
		cell.setBackgroundColor(new Color(214, 214, 214));
		cell.setBorderColor(new Color(214, 214, 214));
		cell.setPadding(8f);
		
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	 
		tabla3.addCell(cell);
		tabla3.addCell(factura.getTotal().toString());
		
		document.add(tabla3);
		
	}
	
	
	
}
