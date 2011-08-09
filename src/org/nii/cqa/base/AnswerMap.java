package org.nii.cqa.base;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Vector;

import org.nii.cqa.web.shared.WebAnswerMap;

public class AnswerMap extends HashMap<Integer, List<List<Integer>>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5333557672963251276L;
	
	public WebAnswerMap webConvert() {
		WebAnswerMap webMap = new WebAnswerMap();
		
		Iterator<Integer> it = this.keySet().iterator();
		while (it.hasNext()) {
			int id = it.next();
			List<List<Integer>> ansList = this.get(id);
			
			Vector<Vector<String>> webAnsList = new Vector<Vector<String>>();
			Iterator<List<Integer>> ansIt = ansList.iterator();
			while (ansIt.hasNext()) {
				List<Integer> ans = ansIt.next();
				Vector<String> webAns = new Vector<String>();
				
				Iterator<Integer> localIt = ans.iterator();
				while (localIt.hasNext()) {
					webAns.add(SymTable.getSym(localIt.next()));
				}
				
				webAnsList.add(webAns);
			}
			
			webMap.put(id, webAnsList);
		}
		
		return webMap;
	}
	
	
	/**
	 * converts the mapped answer list to String 
	 */
	@Override
	public String toString() {
		
		StringBuilder strMap = new StringBuilder();
		
		Iterator<Integer> it = this.keySet().iterator();
		while (it.hasNext()) {
			int id = it.next();
			List<List<Integer>> ansList = this.get(id);
			
			Vector<Vector<String>> strAnsList = new Vector<Vector<String>>();
			Iterator<List<Integer>> ansIt = ansList.iterator();
			while (ansIt.hasNext()) {
				List<Integer> ans = ansIt.next();
				Vector<String> strAns = new Vector<String>();
				
				Iterator<Integer> localIt = ans.iterator();
				while (localIt.hasNext()) {
					strAns.add(SymTable.getSym(localIt.next()));
				}
				
				strAnsList.add(strAns);
			}
			
			strMap.append((id +"\n"+ strAnsList+"\n--------------------------------------\n"));
			
		}
		
		return strMap.toString();
	}

}
