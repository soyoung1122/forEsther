package com.forEsther.controller.seriallotcontroller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.forEsther.service.seriallotservice.SerialLotService;
import com.forEsther.vo.seriallotvo.SerialLotVO;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/serialLot")
public class SerialLotController {
	@Autowired
	private SerialLotService service;
	
	// getList
	@GetMapping("/serialLotList")
	public void list(Model model) {
		log.info("getList...........");
		model.addAttribute("list", service.getList());
	}
	
	// get
	@GetMapping(value = {"/serialLotGet", "/serialLotModify"})
	public void get(@RequestParam("serial_lot_code") String serial_lot_code, Model model) {
		log.info("get...........");
		model.addAttribute("get", service.get(serial_lot_code));
	}
	
	// Get register
	@GetMapping("/serialLotRegister")
	public void register() {

	}
	
	// Post register
	@PostMapping("/serialLotRegister")
	public String register(
			@RequestParam("item_name") String item_name, 
			@RequestParam("item_code") String item_code, 
			@RequestParam("supplier") String supplier, 
			@RequestParam("stock_quantity") String stock_quantity, 
			@RequestParam("expiration_date") String expiration_date, 
			@RequestParam("related_invoice") String related_invoice, 
	        RedirectAttributes rttr) {

		log.info("start register...........");
		SerialLotVO serial_lot = new SerialLotVO();
		
		// String 형식의 날짜를 Date 타입으로 변환
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    
	    try {
	        Date expirationDate = format.parse(expiration_date);
	        
		    serial_lot.setExpiration_date(expirationDate);
		    
	        // item_code와 expiration_date를 이용하여 serial_lot_code를 생성
	        String serialLotCode = item_code + "-" + new SimpleDateFormat("yyyyMMdd").format(expirationDate);
	        serial_lot.setSerial_lot_code(serialLotCode);
	        log.info("serialLotCode....................." + serialLotCode);
	        
	        // 나머지 serial_lot 객체 set
	        serial_lot.setItem_code(item_code);
	        serial_lot.setItem_name(item_name);
	        serial_lot.setRelated_invoice(related_invoice);
	        serial_lot.setStock_quantity(Integer.parseInt(stock_quantity));
	        serial_lot.setSupplier(supplier);

	        log.info("register..........." + serial_lot);

	        service.register(serial_lot);

	        rttr.addFlashAttribute("result", serial_lot);

	        return "redirect:/serialLot/serialLotList";
	    } catch (Exception e) {
	        // 날짜 형식 변환 실패 시 처리할 예외 처리
	        e.printStackTrace();
	        log.info(e);
	        return "redirect:/serialLot/serialLotList"; // 또는 다른 에러 처리 방법
	    }
	}

	
	
	// modify
	@PostMapping("/serialLotModify")
	public String modify(
						@RequestParam("serial_lot_code") String serial_lot_code,
						@RequestParam("item_name") String item_name,
						@RequestParam("stock_quantity") String stock_quantity,
						@RequestParam("expiration_date") String expiration_date,
						RedirectAttributes rttr) {
		// serialLotVO 객체 생성
		SerialLotVO serial_lot = new SerialLotVO();
		serial_lot.setSerial_lot_code(serial_lot_code);
		serial_lot.setItem_name(item_name);
		serial_lot.setStock_quantity(Integer.parseInt(stock_quantity));
		
		// String 형식의 날짜를 Date 타입으로 변환
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date expirationDate;
		try {
			expirationDate = format.parse(expiration_date);
			serial_lot.setExpiration_date(expirationDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		log.info("modify............." + serial_lot);
	    
		service.modify(serial_lot);
		if(service.modify(serial_lot)) {
			rttr.addFlashAttribute("result", serial_lot);
		}
		return "redirect:/serialLot/serialLotList";
	}
}
