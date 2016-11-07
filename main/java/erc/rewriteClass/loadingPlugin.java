package erc.rewriteClass;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

//@TransformerExclusions({"erc.rewriteClass"})
@MCVersion("1.7.10")
public class loadingPlugin implements IFMLLoadingPlugin {
	
	static File location;
	
	public String[] getLibraryRequestClass() {
		return null;
	}

    @Override
    public String[] getASMTransformerClass() {
    	return new String[] {"erc.rewriteClass.classTransformer"};
    }
    @Override
    public String getModContainerClass() {
    	return "erc.rewriteClass.modContainer";
    }
    @Override
    public String getSetupClass() {
    	return null;
    }
    
    // IFMLLoadingPlugin のメソッドです。(IFMLCallHook にも同じシグネチャーのメソッドがありますが、違います)
    // 今回は coremod 自身の jar ファイルパスを取得しています。これは後述のトランスフォーマークラスで、
    // jarから置換用クラスを取得しているためで、そのような処理を行わないのであれば何も実装しなくても構いません。
    // 
    // なお、IFMLLoadingPlugin のメソッドとして呼ばれた際は、"mcLocation"、"coremodList"、"coremodLocation" の3つ、
    // IFMLCallHook のメソッドとして呼ばれた際は、"classLoader" がマップに設定されています。(FML#511現在)
    // 
    // 渡されるマップの中身は、cpw.mods.fml.relauncher.RelaunchLibraryManager の実装からも確認する事が出来ます。	TODO
    @Override
    public void injectData(Map<String, Object> data) {
    	 if (data.containsKey("coremodLocation"))
         {
             location = (File) data.get("coremodLocation");
         }
    }

	@Override
	public String getAccessTransformerClass() {
		return "erc.rewriteClass.classTransformer";
	}
}
