package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;

@Component
public class PageListener implements MessageListener {

	@Autowired
	private ItemPageService itemPageService;

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		String text;
		try {
			text = textMessage.getText();
			System.out.println("接收到消息：" + text);
			boolean b = itemPageService.genItemHtml(Long.parseLong(text));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
