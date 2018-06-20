package downloadNews;

import dataStructure.itemf;
import ikvOperation.IKVOperationv3Test;

public class GetUseFulId {

	static IKVOperationv3Test opt = new IKVOperationv3Test();

	public static void main(String[] args) {
		// for (int i = 11961700; i > 10000000; i-=100000) {
		for (int i = 11961700; i > 10000000; i -= 100000) {
			String key = "cmpp_" + i;

			try {
				itemf it = opt.returnItemfByKey(key);
				System.out.println(it.getID() + "存在");

			} catch (Exception e) {
				System.err.println(key + "不存在");
				e.printStackTrace();
			}

		}
		System.exit(0);
	}
}
