package com.adms.batch.job;



public class TEST {

	public static void main(String[] args) {
		try {
			Object[] objs = new Object[4];

			objs[0] = "a";
			objs[1] = "b";
			
			System.out.println("len: " + objs.length);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void test(String x) {
		String v = x.replaceAll(" ", "");
		if(x.contains(">")) {
			v = v.substring(v.indexOf(">") + 1, v.indexOf("%"));
			System.out.println("Greater than " + v + " percents");
		} else if(x.contains("≤")) {
			v = v.substring(v.indexOf("≤") + 1, v.indexOf("%"));
			System.out.println("Less or equal " + v + " percents");
		} else {
			System.out.println("Not avialable");
		}
	}
}
