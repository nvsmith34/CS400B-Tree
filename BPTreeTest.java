
import org.junit.jupiter.api.BeforeEach;
import org.junit.After;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
public class BPTreeTest {
	static BPTree<Integer,String> tree;
	
	@BeforeEach
	public void setUp() throws Exception {
        tree = new BPTree<Integer,String>(3);
   }
	
	@Test
	public void test1() {
		tree.insert(5, "five");
		tree.insert(4, "four");
		tree.insert(3,"Three");


		tree.insert(6, "six");
		tree.insert(7, "seven");
		tree.insert(8, "eighth");
		tree.insert(8, "eighth");
	    tree.insert(8, "eighth");
		tree.insert(9, "9");
		tree.insert(10, "10");
		tree.insert(11, "11");
		tree.insert(12, "12");
		tree.insert(1, "1");
		tree.insert(2,"2");
		tree.insert(13, "13");
		tree.insert(14, "14");
		tree.insert(15, "15");
		tree.insert(16, "16");
		tree.insert(17, "17");
		tree.insert(18, "18");
		tree.insert(19, "19");
		tree.insert(20, "20");
		tree.insert(21, "21");
		tree.insert(22,"22");
		tree.insert(23, "23");
		List<String> result = tree.rangeSearch(7, ">=");
		
			System.out.print(result.toString());
		
		
		
	}
}
