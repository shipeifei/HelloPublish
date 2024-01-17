package com.mmy.remotecontrol.util.azlist;



import com.mmy.remotecontrol.bean.BrandBean;

import java.util.Comparator;

public class LettersComparatorBrandBean implements Comparator<AZItemEntity<BrandBean>> {

	public int compare(AZItemEntity<BrandBean> o1, AZItemEntity<BrandBean> o2) {
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
