package godsoft.mbl.datagokr.suwon.service.impl;

import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import godsoft.mbl.datagokr.suwon.service.GodsoftMblDatagokrSuwon04Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class GodsoftMblDatagokrSuwon04ServiceImpl implements
		GodsoftMblDatagokrSuwon04Service {

	@Override
	public void cCTV(Map<String, String> vo, ModelMap model) throws Exception {

		// http://27.101.101.31/openapi-data/service/Cctv/cCTV?pageNo=1&numOfRows=10&ServiceKey=tI0v5%2FT30d3u4%2BCtrSld9G6gvMmUkDWxX1pQ09hdEY1EC6vQWlXjTGxmBabd%2B0k8upYXOoWqLN1nBI0Y158dVA%3D%3D
		String url = "http://27.101.101.31/openapi-data/service/Cctv/cCTV?ServiceKey=tI0v5%2FT30d3u4%2BCtrSld9G6gvMmUkDWxX1pQ09hdEY1EC6vQWlXjTGxmBabd%2B0k8upYXOoWqLN1nBI0Y158dVA%3D%3D";

		Connection connection = Jsoup.connect(url);

		// connection.timeout(300000);

		data(vo, model, connection);

		document(vo, model, connection);

		model.addAttribute("htmlTitle", "수원시 불법주정차 CCTV");

		model.addAttribute("vo", vo);
	}

	private void data(Map<String, String> vo, ModelMap model,
			Connection connection) throws Exception {
		String pageNo = MapUtils.getString(vo, "pageNo");
		if (StringUtils.isEmpty(pageNo)) {
			vo.put("pageNo", "1");
		}

		connection.data(vo);
	}

	private void document(Map<String, String> vo, ModelMap model,
			Connection connection) throws Exception {

		Document document = connection.get();
		// Document document = connection.post();

		Map<String, Object> response = new HashMap<String, Object>();
		header(vo, model, document, response);
		body(vo, model, document, response);
		items(vo, model, document, response);
		model.addAttribute("response", response);

		paginationInfo(vo, model, document, response);
	}

	private void header(Map<String, String> vo, ModelMap model,
			Document document, Map<String, Object> response) throws Exception {

		Elements headerElements = document.select("response > header");

		Map<String, Object> header = new HashMap<String, Object>();

		header.put("resultCode", headerElements.select("resultCode").text());
		header.put("resultMsg", headerElements.select("resultMsg").text());

		response.put("header", header);
	}

	private void body(Map<String, String> vo, ModelMap model,
			Document document, Map<String, Object> response) throws Exception {

		Elements headerElements = document.select("response > body");

		Map<String, Object> body = new HashMap<String, Object>();

		body.put("numOfRows", headerElements.select("numOfRows").text());
		body.put("pageNo", headerElements.select("pageNo").text());
		body.put("totalCount", headerElements.select("totalCount").text());

		response.put("body", body);
	}

	private void items(Map<String, String> vo, ModelMap model,
			Document document, Map<String, Object> response) throws Exception {

		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

		Elements elements = document.select("response > body > items > item");

		for (Element element : elements) {
			Map<String, Object> item = new HashMap<String, Object>();

			item.put("equpSn", element.select("equpSn").text());
			item.put("gpsCrdntX", element.select("gpsCrdntX").text());
			item.put("gpsCrdntY", element.select("gpsCrdntY").text());
			item.put("instDong", element.select("instDong").text());
			item.put("instPont", element.select("instPont").text());

			items.add(item);
		}

		response.put("items", items);
	}

	private void paginationInfo(Map<String, String> vo, ModelMap model,
			Document document, Map<String, Object> response) throws Exception {

		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(MapUtils.getIntValue(vo, "pageNo"));
		paginationInfo.setRecordCountPerPage(10);
		paginationInfo.setPageSize(1);

		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) response.get("body");

		paginationInfo.setTotalRecordCount(MapUtils.getIntValue(body,
				"totalCount"));

		model.addAttribute("paginationInfo", paginationInfo);
	}

}
