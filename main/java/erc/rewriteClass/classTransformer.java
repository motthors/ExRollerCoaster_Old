package erc.rewriteClass;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.sun.org.apache.bcel.internal.generic.ALOAD;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;

public class classTransformer implements IClassTransformer {

	// 改変対象のクラスの完全修飾名です。
    // 後述でMinecraft.jar内の難読化されるファイルを対象とする場合の簡易な取得方法を紹介します。
    private static final String TARGET_CLASS_NAME = "net.minecraft.client.renderer.EntityRenderer";
     static int counter = 0;
 	@Override
 	public byte[] transform(String name, String transformedName, byte[] bytes) 
 	{
// 		FMLRelaunchLog.info("MFWTransformLog : Classname'%s'++'%s'", name, transformedName);
// 		if(FMLLaunchHandler.side().isServer())return bytes;
 		
 		if (TARGET_CLASS_NAME.equals(transformedName))
 		{
 			ClassReader cr = new ClassReader(bytes); 	// byte配列を読み込み、利用しやすい形にする。
	 		ClassWriter cw = new ClassWriter(cr, 1); 	// これのvisitを呼ぶことによって情報が溜まっていく。
	 		ClassVisitor cv = new ClassAdapter(cw); 	// Adapterを通して書き換え出来るようにする。
	 		cr.accept(cv, 0); 							// 元のクラスと同様の順番でvisitメソッドを呼んでくれる
	 		return cw.toByteArray(); 					// Writer内の情報をbyte配列にして返す。
 		}
 		else if ("net.minecraft.entity.player.EntityPlayer".equals(transformedName))
 		{
 			ClassReader cr = new ClassReader(bytes); 	// byte配列を読み込み、利用しやすい形にする。
	 		ClassWriter cw = new ClassWriter(cr, 1); 	// これのvisitを呼ぶことによって情報が溜まっていく。
	 		ClassVisitor cv = new ClassAdapter_GetOff(cw); 	// Adapterを通して書き換え出来るようにする。
	 		cr.accept(cv, 0); 							// 元のクラスと同様の順番でvisitメソッドを呼んでくれる
	 		return cw.toByteArray(); 					// Writer内の情報をbyte配列にして返す。
 		}
 		else
 			return bytes;
 	}
     
 	
 	public static class ClassAdapter extends ClassVisitor 
	{
		public ClassAdapter(ClassVisitor cv)
		{
			super(ASM5, cv);
		}

		/**
		 * メソッドについて呼ばれる。
		 * 
		 * @param access  {@link Opcodes}に載ってるやつ。publicとかstaticとかの状態がわかる。
		 * @param name	メソッドの名前。
		 * @param desc メソッドの(引数と返り値を合わせた)型。
		 * @param signature   ジェネリック部分を含むメソッドの(引数と返り値を合わせた)型。ジェネリック付きでなければおそらくnull。
		 * @param exceptions  throws句にかかれているクラスが列挙される。Lと;で囲われていないので  {@link String#replace(char, char)}で'/'と'.'を置換してやればOK。
		 * @return ここで返したMethodVisitorのメソッド群が適応される。  ClassWriterがセットされていればMethodWriterがsuperから降りてくる。
		 */
		private static final String TARGET_TRANSFORMED_NAME = "func_78467_g";
		private static final String TARGET_Original_NAME = "orientCamera";
		private static final String TARGET_DESC = "(F)V";
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			boolean flag = false;
			flag |= TARGET_TRANSFORMED_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
			flag |= TARGET_Original_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
			if(flag && TARGET_DESC.equals(desc))
			{
				return new MethodAdapter(super.visitMethod(access, name, desc, signature, exceptions));
			}
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}
 	
 	public static class MethodAdapter extends MethodVisitor {
		public MethodAdapter(MethodVisitor mv) 
		{
			super(ASM5, mv);
		}

		/**
		 * int型変数等の操作時に呼ばれる。
		 * 
		 * @param opcode   byteの範囲で扱えるならBIPUSH、shortの範囲で扱えるならSIPUSHが入っている。
		 * @param operand    shortの範囲に収まる値が入っている。
		 */
		public static int MethodCount = 0;
//		private static final String TARGET_CLASS_NAME = "net/minecraft/client/enderer/RenderGlobal";
//		private static final String TARGET_TRANSFORMED_NAME = "func_72719_a"; 
//		private static final String TARGET_Orginal_NAME = "sortAndRender";
//		private static final String TARGET_DESC = "(Lnet/minecraft/entity/EntityLivingBase;ID)I";
		@Override
		public void visitVarInsn(int opcode, int var)
		{
			if(MethodCount++==0)
			{
				super.visitVarInsn(Opcodes.FLOAD, 1);
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "erc/manager/ERC_CoasterAndRailManager", "CameraProc", "(F)V", false);
			}
			super.visitVarInsn(opcode, var);
		}
	}
 	
 	public static class ClassAdapter_GetOff extends ClassVisitor 
	{
		public ClassAdapter_GetOff(ClassVisitor cv){super(ASM5, cv);}

		private static final String TARGET_TRANSFORMED_NAME = "func_70098_U";
		private static final String TARGET_Original_NAME = "updateRidden";
		private static final String TARGET_DESC = "()V";
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			boolean flag = false;
			flag |= TARGET_TRANSFORMED_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
			flag |= TARGET_Original_NAME.equals(mapMethodName(TARGET_CLASS_NAME, name, desc));
			if(flag && TARGET_DESC.equals(desc))
			{
				return new MethodAdapter_GetOff(super.visitMethod(access, name, desc, signature, exceptions));
			}
			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}
 	
 	public static class MethodAdapter_GetOff extends MethodVisitor {
		public MethodAdapter_GetOff(MethodVisitor mv) {super(ASM5, mv);}

		public static int MethodCount = 0;
		@Override
		public void visitVarInsn(int opcode, int var)
		{
			if(opcode == Opcodes.ALOAD && var == 0)
			{
				MethodCount++;
				if(MethodCount == 1)
				{
					super.visitVarInsn(Opcodes.ALOAD, 0);
					super.visitMethodInsn(Opcodes.INVOKESTATIC, "erc/manager/ERC_CoasterAndRailManager", "GetOffAndButtobi", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false);
				}
			}
			super.visitVarInsn(opcode, var);
		}
		
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
		{
			super.visitMethodInsn(opcode, owner, name, desc, itf);
			if(name.equals("setSneaking") || name.equals("func_70095_a"))
			{
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "erc/manager/ERC_CoasterAndRailManager", "motionactive", "()V", false);
			}
		}
	}
 	
	/**
	 * メソッドの名前を易読化(deobfuscation)する。
	 */
	public static String mapMethodName(String owner, String methodName, String desc) {
		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(unmapClassName(owner), methodName, desc);
	}
	
	/**
	 * クラスの名前を難読化(obfuscation)する。
	 */
	public static String unmapClassName(String name) {
		return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
	}

}
