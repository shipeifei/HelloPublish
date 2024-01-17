package com.mmy.remotecontrol.util.azlist;



import com.mmy.remotecontrol.bean.ModelNoBean;

import java.util.Comparator;

public class LettersComparatorModelNoBean implements Comparator<AZItemEntity<ModelNoBean>> {

	public int compare(AZItemEntity<ModelNoBean> o1, AZItemEntity<ModelNoBean> o2) {
		if (o1.getSortLetters().equals("@")
			|| o2.getSortLetters().equals("#")) {
			return 1;
		} else if (o1.getSortLetters().equals("#")
				   || o2.getSortLetters().equals("@")) {
			return -1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
