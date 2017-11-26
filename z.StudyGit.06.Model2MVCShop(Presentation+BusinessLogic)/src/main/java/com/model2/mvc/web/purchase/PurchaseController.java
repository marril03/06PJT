package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.user.UserService;

@Controller
public class PurchaseController {
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;

	
	public PurchaseController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml 참조 할것
	//==> 아래의 두개를 주석을 풀어 의미를 확인 할것
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
		
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	@RequestMapping("/addPurchaseView.do")
	public String addPurchaseView(@RequestParam("prodNo") int prodNo,Model model,HttpSession session) throws Exception {
		System.out.println("/addPurchaseView.do");
		User user = (User)session.getAttribute("user");
		Product product = productService.getProduct(prodNo);
		
		model.addAttribute("product", product);
		model.addAttribute("user", user);
		
		System.out.println("user:::"+user);
		System.out.println("product:::"+product);
		
		return "forward:/purchase/addPurchaseView.jsp";
	}
	
	
	@RequestMapping("/addPurchase.do")
	public String addPurchase(@ModelAttribute("pruchase") Purchase purchase,
							  @RequestParam("prodNo") int prodNo,
							  HttpSession session,
							  Model model ) throws Exception {

		System.out.println("/addPurchase.do");
		User user = (User)session.getAttribute("user");
		Product product = productService.getProduct(prodNo);

		purchase.setBuyer(user);
		purchase.setPurchaseProd(product);
		purchase.setTranCode("0");
		
		//Business Logic
		System.out.println("purchase:::"+purchase);
		System.out.println("product:::"+product);
		System.out.println("user:::"+user);
		
		model.addAttribute("purchase", purchase);
		purchaseService.addPurchase(purchase);
		
		return "forward:/purchase/addPurchase.jsp";
	}
	
	@RequestMapping("/listPurchase.do")
	public String listPurchase( @ModelAttribute("search") Search search , 
								Model model , 
								HttpSession session,
								HttpServletRequest request
								) throws Exception{
		
		System.out.println("/listPurchase.do");
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		User user = (User)session.getAttribute("user");

		// Business logic 수행
		Map<String , Object> map = purchaseService.getPurchaseList(search, user.getUserId());
	
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println("resultPage:::::"+resultPage);

		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		
		return "forward:/purchase/listPurchase.jsp";
	}
	
	@RequestMapping("/getPurchase.do")
	public String getProduct( @RequestParam("tranNo") int tranNo , Model model ) throws Exception {
		
		System.out.println("/getPurchase.do");
		//Business Logic
		Purchase purchase = purchaseService.getPurchase(tranNo);
		// Model 과 View 연결
		model.addAttribute("purchase", purchase);
		return "forward:/purchase/getPurchase.jsp";
	}
	
	@RequestMapping("/updatePurchaseView.do")
	public String updatePurchaseView(@RequestParam("tranNo") int tranNo , Model model ) throws Exception {
		
		System.out.println("/updatePurchaseView.do");
		//Business Logic
		Purchase purchase = purchaseService.getPurchase(tranNo);
		// Model 과 View 연결
		model.addAttribute("purchase", purchase);
		return "forward:/purchase/updatePurchaseView.jsp";
	}
	
	@RequestMapping("/updatePurchase.do")
	public String updatePurchase(@ModelAttribute("purchase") Purchase purchase,
								 @RequestParam("tranNo") int tranNo, 
								 Model model ) throws Exception {
		
		System.out.println("/updatePurchase.do");
		//Business Logic
		
		purchaseService.updatePurchase(purchase);
		purchase=purchaseService.getPurchase(tranNo);
		model.addAttribute("purchase", purchase);

		return "forward:/purchase/updatePurchase.jsp";
	}
	
	@RequestMapping("/updateTranCode.do")
	public String updateTranCode(@RequestParam("tranNo") int tranNo,
								 @RequestParam("TranCode") String TranCode, 
								 Model model ) throws Exception {
		
		System.out.println("/updateTranCode.do");
		//Business Logic
		System.out.println("tranNo::"+tranNo+"   proTrnaCode::"+TranCode);
		Purchase purchase = purchaseService.getPurchase(tranNo);
		purchase.setTranCode(TranCode);
		
		purchaseService.updateTranCode(purchase);
		
		model.addAttribute("purchase", purchase);

		return "forward:/listPurchase.do";
	}
	
	@RequestMapping("/updateTranCodeByProd.do")
	public String updateTranCodeByProd(@RequestParam("prodNo") int prodNo,
									   @RequestParam("proTranCode") String proTranCode, 
								 	   Model model ) throws Exception {
		
		System.out.println("/updateTranCodeByProd.do");
		//Business Logic
		System.out.println("prodNo::"+prodNo+"   proTrnaCode::"+proTranCode);
		Purchase purchase = purchaseService.getPurchase2(prodNo);
		purchase.setTranCode(proTranCode);
		purchaseService.updateTranCode(purchase);
		
		model.addAttribute("purchase", purchase);

		return "forward:/listProduct.do?menu=manage";
	}
}
