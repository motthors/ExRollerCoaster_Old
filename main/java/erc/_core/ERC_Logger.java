package erc._core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ERC_Logger {
	 
	public static Logger logger = LogManager.getLogger("ERC");
 
	/*
	 * 以下のメソッドはわざわざ呼び出さなくても、
	 * TutorialLogger.logger.trace(msg);等で呼び出せば事足りるものではある。
	 * 出力するログに、定型文やエラーログなどを含めて出したい場合は、以下のようなメソッドを好きにカスタマイズして、
	 * ログを出したい場所でこのクラスのメソッドを呼ぶようにすると、少し手間を省略できる。
	 */
	public static void trace(String msg) {
		ERC_Logger.logger.trace(msg);
	}
 
	public static void info(String msg) {
		ERC_Logger.logger.info(msg);
	}
 		
	public static void warn(String msg) {
		ERC_Logger.logger.warn(msg);
	}
	
	public static void debugInfo(String msg) {
		info(msg);
	}
 
}