package cn.lichengwu.jvm.feature;

public class WhenToCollectMemory {

	/**
	 * VM args: -verbose:gc -Xmx12m -Xms12m
	 * 
	 * @author lichengwu
	 * @created 2012-3-15
	 * 
	 */
	public void test1() {
		System.out.println("begin");
		@SuppressWarnings("unused")
		byte[] waste = new byte[6 * 1024 * 1024];
		System.gc();
		System.out.println("end");
	}

	@SuppressWarnings("unused")
	public void test2() {
		{
			byte[] waste = new byte[6 * 1024 * 1024];
		}
		int new_var = 0;
		System.gc();
	}

	/**
	 * <pre>
	 *  begin 
	 *  [GC 6514K->6480K(11776K), 0.0010729 secs] [Full GC 6480K->6356K(11776K), 0.0077002 secs] 
	 *  end
	 *  结果解释：
	 *  1.gc没有收集掉内存，是因为在执行test1()方法的时，虚拟机把waste的引用放在虚拟机栈的局部变量表中，
	 *  由于下文没有其他任何引用改变waste在基本变量表的引用，所以只有方法执行完成后，回收剧本变量表时才能回收数据。
	 *  2.test2()由于waste过了作用范围，所以 变量a占用了waste的solr槽，所以waste不在被引用，直接回收：
	 *  begin
	 * [GC 6514K->6464K(11776K), 0.0017307 secs]
	 * [Full GC 6464K->212K(11776K), 0.0071686 secs]
	 * end
	 * </pre>
	 */
	public static void main(String[] args) {
		WhenToCollectMemory wtcm = new WhenToCollectMemory();
//		 wtcm.test1();
		wtcm.test2();
	}
}
