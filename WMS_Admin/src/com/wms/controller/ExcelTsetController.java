package com.wms.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.wms.bean.Test;
import com.wms.common.PathService;
import com.wms.service.ExcelTsetService;

@Controller
@RequestMapping("/excel")
public class ExcelTsetController {
	
	@Resource
	private ExcelTsetService excelservice;
	
	/**
	 * 导出test测试表表格
	 * @param page
	 * @param rows
	 */
	@RequestMapping("/deriveExcel")
	public void customerExcel(HttpServletRequest request,HttpServletResponse response){
		System.out.println("=======================================");
		 // 第一步，创建一个webbook，对应一个Excel文件  
        HSSFWorkbook wb = new HSSFWorkbook();  
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
        HSSFSheet sheet = wb.createSheet("导出测试表");  
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
        HSSFRow row = sheet.createRow(0);  
        // 第四步，创建单元格，并设置值表头 设置表头居中  
        HSSFCellStyle style = wb.createCellStyle();  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
  
        HSSFCell cell = row.createCell(0);  
        cell.setCellValue("编号");  
        cell.setCellStyle(style);
        
        cell = row.createCell(1); 
        cell.setCellValue("姓名");  
        cell.setCellStyle(style);
        
        cell = row.createCell(2);  
        cell.setCellValue("班级");  
        cell.setCellStyle(style);  
        
        cell = row.createCell(3);  
        cell.setCellValue("笔试成绩");  
        cell.setCellStyle(style);  
        
        cell = row.createCell(4);  
        cell.setCellValue("机试成绩");  
        cell.setCellStyle(style);
        
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，  
		List<Test> list=excelservice.queryAll();
        for (int i = 0; i < list.size(); i++)
        {  
            row = sheet.createRow((int) i + 1);  
            // 第四步，创建单元格，并设置值  
            row.createCell(0).setCellValue(i+1);  
            row.createCell(1).setCellValue(list.get(i).getName());  
            row.createCell(2).setCellValue(list.get(i).getBanji());
            row.createCell(3).setCellValue(list.get(i).getBscj()); 
            row.createCell(4).setCellValue(list.get(i).getJscj());
        }  
        // 第六步，将文件存到指定位置  
        try  
        {  
        	FileOutputStream fout = new FileOutputStream(PathService.Path(request)+"/"+"customer.xls");  
            wb.write(fout);  
            fout.close();  
            ServletContext context = request.getSession().getServletContext();  
		    //通过context方式直接获取文件的路径  
		    String path = context.getRealPath("/导出测试表.xls");  
		    //获取文件名  
		    String filename = path.substring(path.lastIndexOf("\\")+1);  
		    //将文件名进行URL编码  
		    filename = URLEncoder.encode(filename,"utf-8");  
		    //告诉浏览器用下载的方式打开图片  
		    response.setHeader("content-disposition", "attachment;filename="+filename);  
		    //将图片使用字节流的形式写给客户机  
		    @SuppressWarnings("resource")
			InputStream is = new FileInputStream(PathService.Path(request)+"/"+"customer.xls"); //从这个路径下读取文件 
		    OutputStream out = response.getOutputStream();  
		    byte[] buffer = new byte[1024];  
		    int len = 0;  
		    while((len=is.read(buffer))!=-1){  
		        out.write(buffer, 0, len);  
		    }
        }  
        catch (Exception e)
        {  
            e.printStackTrace();
        }
	}
	
	/**
	 * 导入test测试表表格
	 * @param page
	 * @param rows
	 * @throws IOException 
	 */
	@RequestMapping("toLead")
	@ResponseBody
	public Map<String, Object> toLeadExcel(HttpServletRequest request,HttpServletResponse response) throws IOException{
		Map<String , Object> map= new HashMap<>();
		MultipartHttpServletRequest mult = (MultipartHttpServletRequest) request;
		//获取文件
		MultipartFile file = mult.getFile("gain");
	    String fileName = file.getOriginalFilename();
	    String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
	    //判断是否为excel文件	    
	    if("xls".equals(formatName) || "xlsm".equals(formatName) || "xlsx".equals(formatName)){
	    	File path=new File(request.getSession().getServletContext().getRealPath("/")+"upload");
			if(!path.exists()){
				path.mkdirs();
			}
			
			System.out.println("保存路径："+path);
			FileOutputStream fos=new FileOutputStream(path+"/"+fileName);
			
			fos.write(file.getBytes());
			fos.flush();
			fos.close();
			
	    	map.put("result","1");
	    }else{
	    	map.put("result","2");
	    }
		return map;
	}
	
}