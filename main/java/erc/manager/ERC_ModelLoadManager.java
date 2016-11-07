package erc.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import erc._core.ERC_CONST;
import erc._core.ERC_Logger;
import erc.model.ERC_ModelAddedRail;
import erc.model.ERC_ModelCoaster;
import erc.model.ERC_ModelDefaultRail;
import erc.model.Wrap_RailRenderer;
import erc.tileEntity.TileEntityRailBase;
import erc.tileEntity.TileEntityRailBranch2;
import erc.tileEntity.TileEntityRailConstVelosity;
import erc.tileEntity.TileEntityRailDetector;
import erc.tileEntity.TileEntityRailInvisible;
import erc.tileEntity.TileEntityRailRedstoneAccelerator;
import erc.tileEntity.Wrap_TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;

public class ERC_ModelLoadManager {
	
	public static class ModelPack{
		public IModelCustom Model;
		public ResourceLocation Tex;
		public String IconStr;
		public ModelOptions op;
		public ModelPack(IModelCustom m, ResourceLocation t, String ic, ModelOptions op)
		{
			Model = m; 
			Tex = t; 
			IconStr = ic;
			if(op==null)this.op = new ModelOptions();
			else this.op = op;
		}
	}
	
//	public static class ModelPack2{
//		public IModelCustom MainModel;
//		public IModelCustom ConnectModel;
//		public ResourceLocation MainTex;
//		public ResourceLocation ConnectTex;
//		public String IconStr;
//		public String ConnectIconStr;
//		public ModelOptions op[];
//		public ModelPack2(IModelCustom mm, ResourceLocation mt, IModelCustom cm, ResourceLocation ct, String ic, String cic)
//		{
//			MainModel = mm; ConnectModel = cm; MainTex = mt; ConnectTex = ct; IconStr = ic; ConnectIconStr = cic;
//			op = new ModelOptions[2];
//			for(int i=0; i<2; ++i) op[i] = new ModelOptions();
//		}
//		public ERC_ModelCoaster getModelSet(int flag)
//		{
//			switch(flag)
//			{
//			case 0 : return new ERC_ModelCoaster(MainModel, MainTex);
//			case 1 : return new ERC_ModelCoaster(ConnectModel, ConnectTex);
//			}
//			return null;
//		}
//	}
	
	public static class ModelOptions{	
		// 速度制御
		public float Weight = 1.0f;
		// 車両のサイズ
		public float Width = 2.0f;
		public float Height = 2.0f;
		public float Length = 1.5f;	//接続長
		// 座席情報
		public boolean canRide = true;
		public int SeatNum = 0;
		public float[] offsetX;
		public float[] offsetY;
		public float[] offsetZ;
		public float[] rotX;
		public float[] rotY;
		public float[] rotZ;
		public float[] size;
		public ModelOptions(){}
		public void setSeatNum(int num){
			SeatNum = num;
			offsetX = new float[num];
			offsetY = new float[num];
			offsetZ = new float[num];
			rotX = new float[num];
			rotY = new float[num];
			rotZ = new float[num];
			size = new float[num]; for(int i=0;i<num;++i)size[i]=1.0f;
		}
		public void WriteBuf(ByteBuf buf){
			buf.writeBoolean(canRide);
			buf.writeFloat(Width);
			buf.writeFloat(Height);
			buf.writeFloat(Length);
			buf.writeInt(SeatNum);
			for(int i=0;i<SeatNum;++i)
			{
				buf.writeFloat(offsetX[i]);
				buf.writeFloat(offsetY[i]);
				buf.writeFloat(offsetZ[i]);
				buf.writeFloat(rotX[i]);
				buf.writeFloat(rotY[i]);
				buf.writeFloat(rotZ[i]);
				buf.writeFloat(size[i]);
			}
		}
		public void ReadBuf(ByteBuf buf){
			canRide = buf.readBoolean();
			Width = buf.readFloat();
			Height = buf.readFloat();
			Length = buf.readFloat();
			setSeatNum(buf.readInt());
//			SeatLine = buf.readInt();
			for(int i=0;i<SeatNum;++i)
			{
				offsetX[i] = buf.readFloat();
				offsetY[i] = buf.readFloat();
				offsetZ[i] = buf.readFloat();
				rotX[i] = buf.readFloat();
				rotY[i] = buf.readFloat();
				rotZ[i] = buf.readFloat();
				size[i] = buf.readFloat();
			}
		}
		public void WriteToNBT(NBTTagCompound nbt)
		{
			nbt.setBoolean("op_canride", canRide);
			nbt.setFloat("op_width", Width);
			nbt.setFloat("op_height", Height);
			nbt.setFloat("op_length", Length);
			nbt.setInteger("op_seatnum", SeatNum);
//			nbt.setInteger("op_seatline", SeatLine);
			for(int i=0;i<SeatNum;++i)
			{
				nbt.setFloat("op_seatx"+i, offsetX[i]);
				nbt.setFloat("op_seaty"+i, offsetY[i]);
				nbt.setFloat("op_seatz"+i, offsetZ[i]);
				nbt.setFloat("op_seatrx"+i, rotX[i]);
				nbt.setFloat("op_seatry"+i, rotY[i]);
				nbt.setFloat("op_seatrz"+i, rotZ[i]);
				nbt.setFloat("op_seats"+i, size[i]);
			}
		}
		public void ReadFromNBT(NBTTagCompound nbt)
		{
			canRide = nbt.getBoolean("op_canride");
			Width = nbt.getFloat("op_width");
			Height = nbt.getFloat("op_height");
			Length = nbt.getFloat("op_length");
			setSeatNum(nbt.getInteger("op_seatnum"));
//			SeatLine = nbt.getInteger("op_seatline");
			for(int i=0;i<SeatNum;++i)
			{
				offsetX[i] = nbt.getFloat("op_seatx"+i);
				offsetY[i] = nbt.getFloat("op_seaty"+i);
				offsetZ[i] = nbt.getFloat("op_seatz"+i);
				rotX[i] = nbt.getFloat("op_seatrx"+i);
				rotY[i] = nbt.getFloat("op_seatry"+i);
				rotZ[i] = nbt.getFloat("op_seatrz"+i);
				size[i] = nbt.getFloat("op_seats"+i);
			}
		}
	}

	public static class RailPack{
		public IModelCustom[] RailModels;
		public ResourceLocation[] RailTexs;
		public String IconStr;
		public RailPack(IModelCustom[] Models, ResourceLocation[] texs, String ic)
		{
			RailModels = Models; RailTexs = texs; IconStr = ic;
		}
		public ERC_ModelAddedRail getModelSet(int flag)
		{
			if(flag < 0 || flag > 4) flag=0;
			
			return new ERC_ModelAddedRail(RailModels[flag], RailTexs[flag]);
		}
	}
	
	private static Map<String, IModelCustom> MapModel = new HashMap<String, IModelCustom>();
	private static Map<String, ResourceLocation> MapTex = new HashMap<String, ResourceLocation>();
//	private static List<ModelPack2> ModelPackList = new ArrayList<ModelPack2>();
	private static List<ModelPack> ModelPackList_Main = new ArrayList<ModelPack>();
	private static List<ModelPack> ModelPackList_Connect = new ArrayList<ModelPack>();
	private static List<ModelPack> ModelPackList_Mono = new ArrayList<ModelPack>();
	private static List<RailPack> RailPackList = new ArrayList<RailPack>();	
	
	private static final String AddModelAdd = "./mods/ERC_AddModels/assets/"+ERC_CONST.D_AM+"/";
//	private static final String ExternalFileAdd = "../../../"; // デバッグ時用		TODO
//	private static final String ExternalFileAdd = "../../../../"; // パッケージ化時用
	
	public static void init()
	{	
//    	addObj("../../../../model.obj");
//    	ERC_Logger.info("file.obj ... OK");
    	
    	// コースターデフォ登録
//		ERC_Logger.info("LoadManager Init");
//		addObj(defaultModel);
//		addObj(defaultModel_c);
//		addTexture(defaultTex);
//		ModelPack2 mp = addModelPack2(defaultModel, defaultTex, defaultModel_c, defaultTex, defaultIcon, defaultIcon_c);
//		readOptionsFromFileName(mp, "file.sn1", 0);
//		readOptionsFromFileName(mp, "file.sn1", 1);
		
		//ディレクトリ作成
		File ercfirCoaster = new File(AddModelAdd+"Coaster/");
//		ercfirCoaster.mkdirs();
		File ercfirRail = new File(AddModelAdd+"Rail/");
//		ercfirRail.mkdirs();
		
		//中のファイル走査
		String[] modeldirsC = ercfirCoaster.list();
		String[] modeldirsR = ercfirRail.list();
		
		// 中身見て登録
		if(modeldirsC==null)return;
		for(String strDirName : modeldirsC)
		{
			ERC_Logger.info("folder open:"+"/Coaster/"+strDirName);
			registerCoaster("Coaster/"+strDirName);
		}
		if(modeldirsR==null)return;
		for(String strDirName : modeldirsR)
		{
			ERC_Logger.info("folder open:"+"/Rail/"+strDirName);
			registerRail("Rail/"+strDirName);
		}
	}
	
	private static void registerCoaster(String str)
	{
		File dir = new File(AddModelAdd+str);
		if(!dir.isDirectory())return;
		
		int modelFlags = 0;
		String ObjName = null;//defaultModel;
		String cObjName = null;//defaultModel_c;
		String mObjName = null;
		String TexName = null;//defaultTex;
		String cTexName = null;//defaultTex_c;
		String mTexName = null;
		String IconName = null;//defaultIcon;
		String cIconName = null;//defaultIcon_c;
		String mIconName = null;
		String OptionFileNameMain = "";
		String OptionFileNameConnect = "";
		String OptionFileNameMono = "";
		
		//obj,tex読み込み
		String[] fileNames = dir.list();
		for( String fname : fileNames)
		{
			ERC_Logger.info("file open:"+str+"/"+fname);
//			File file = new File(str+"/"+fname);
			
			//////// model_c.obj 連結コースターのモデル
	        if (fname.matches(".*model_c\\..*.obj$"))
        	{
	        	cObjName=ERC_CONST.D_AM+":"+str+"/"+fname;
	        	OptionFileNameConnect = fname;
	        	addObj(cObjName);
	        	modelFlags |= 2;
        	}
        	//////// model_m.obj 単座コースターのモデル
	        if (fname.matches(".*model_m\\..*.obj$"))
        	{
	        	mObjName=ERC_CONST.D_AM+":"+str+"/"+fname;
	        	OptionFileNameMono = fname;
	        	addObj(mObjName);
	        	modelFlags |= 4;
        	}   
	        //////// model.obj メインコースターのモデル
	        if (fname.matches(".*model\\..*obj$"))
        	{
	        	ObjName=ERC_CONST.D_AM+":"+str+"/"+fname;
	        	OptionFileNameMain = fname;
	        	addObj(ObjName);
	        	modelFlags |= 1;
        	}

	        //////// tex.png メインコースターのテクスチャ
	        if (fname.matches(".*tex\\.*png$"))
        	{
	        	TexName=ERC_CONST.D_AM+":"+str+"/"+fname;
	        	addTexture(TexName);
        	}
	        //////// tex_c.png 連結コースターのテクスチャ
	        if (fname.matches(".*tex_c\\.*png$"))
        	{
	        	cTexName=ERC_CONST.D_AM+":"+str+"/"+fname;
	        	addTexture(cTexName);
        	}
	        //////// tex_m.png 単座コースターのテクスチャ
	        if (fname.matches(".*tex_m\\.*png$"))
        	{
	        	mTexName=ERC_CONST.D_AM+":"+str+"/"+fname;
	        	addTexture(mTexName);
        	}
	        
	        //////// icon.png メインコースターのアイコン
	        if (fname.matches(".*icon\\.*png$"))
        	{
	        	IconName=removeExtention(ERC_CONST.D_AM+":"+"../../"+str+"/"+fname);
//	        	addTexture(IconName);
        	}
        	//////// icon.png 連結コースターのアイコン
	        if (fname.matches(".*icon_c\\.*png$"))
        	{
	        	cIconName=removeExtention(ERC_CONST.D_AM+":"+"../../"+str+"/"+fname);
//	        	addTexture(cIconName);
        	}
	        //////// icon.png 連結コースターのアイコン
	        if (fname.matches(".*icon_m\\.*png$"))
        	{
	        	mIconName=removeExtention(ERC_CONST.D_AM+":"+"../../"+str+"/"+fname);
//	        	addTexture(mIconName);
        	}
		}

//		ModelPack2 mp = addModelPack2(ObjName, TexName, cObjName, cTexName, IconName, cIconName);
		ModelPack mp = null;
		ModelOptions op = new ModelOptions();
		if((modelFlags&1) == 1)
		{
			readOptionsFromFileName(op, OptionFileNameMain);
			mp = new ModelPack(getObj(ObjName), getTex(TexName), IconName, op);
			ModelPackList_Main.add(mp);
		}
		if((modelFlags&2) == 2)
		{
			readOptionsFromFileName(op, OptionFileNameConnect);
			mp = new ModelPack(getObj(cObjName), getTex(cTexName), cIconName, op);
			ModelPackList_Connect.add(mp);
		}
		if((modelFlags&4) == 4)
		{
			readOptionsFromFileName(op, OptionFileNameMono);
			mp = new ModelPack(getObj(mObjName), getTex(mTexName), mIconName, op);
			ModelPackList_Mono.add(mp);
		}
	}
	
	private static void registerRail(String str)
	{
		File dir = new File(AddModelAdd+str);
		if(!dir.isDirectory())return;
		
		String[] Formats = {"n","r","c","d","b"};
		String[] ObjNames = {"null","null","null","null","null"};
		String[] TexNames = {"null","null","null","null","null"};
		String IconName = "";		

//		String OptionFileNameMain = "";
//		String OptionFileNameConnect = "";
		
		//obj,tex読み込み
		String[] objfile = dir.list();
		for( String fname : objfile)
		{
			ERC_Logger.info("filename:"+str+"/"+fname);
//			File file = new File(str+"/"+fname);
			
			for(int i=0;i<Formats.length;++i)
			{
				if(fname.matches(".*\\."+Formats[i]+"\\..*obj"))
				{
					String name = ERC_CONST.D_AM+":"+str+"/"+fname;
					addObj(name);
					ObjNames[i] = name;
					break;
				}
				if(fname.matches(".*\\."+Formats[i]+"\\..*png"))
				{
					String name = ERC_CONST.D_AM+":"+str+"/"+fname;
					addTexture(name);
					TexNames[i] = name;
					break;
				}
			}
			if(fname.matches("^icon\\.png$"))
			{
				IconName=removeExtention(ERC_CONST.D_AM+":"+"../../"+str+"/"+fname);
			}
		}

		// 読み込めなかったモデルは通常レールで描画する
		for(int i=0;i<ObjNames.length;++i)if(ObjNames[i].matches("^null$"))ObjNames[i] = ObjNames[0];
		for(int i=0;i<TexNames.length;++i)if(TexNames[i].matches("^null$"))TexNames[i] = TexNames[0];
		// 通常レールが読み込めてなかったら登録しない
		if(ObjNames[0].matches("^null$"))return;
		
		addRailModelPack(ObjNames, TexNames, IconName);
	}
	
	public static void readOptionsFromFileName(ModelOptions Options, String filename)
	{
		String op;
		filename = removeExtention(filename);//拡張子取り除き
		while( (op = getExtention(filename)) != null )
		{
			if(!op.matches("^[a-zA-Z]+[0-9]+_*-*[0-9]*$")){filename = removeExtention(filename); continue;}
			char flag = op.charAt(0);
			switch(flag)
			{
			case 'L'/*長さ*/: Options.Length = Float.parseFloat(op.substring(1,op.length())) * 0.01f;	break;
			case 'g'/*重さ*/: Options.Weight = Float.parseFloat(op.substring(1,op.length())) * 0.01f;	break;
			case 'w'/*幅　*/: Options.Width = Float.parseFloat(op.substring(1,op.length())) * 0.01f;	break;
			case 'h'/*高さ*/: Options.Height = Float.parseFloat(op.substring(1,op.length())) * 0.01f;	break;
			case 's'/*座席*/: subOpstions(Options, op.charAt(1), op.substring(2, op.length()));		break;
			}
			filename = removeExtention(filename);
		}
	}
	
	public static void subOpstions(ModelOptions op, char flag, String opstr)
	{
		int idx = 0;
		int num = 0;
		boolean numflag;
		if(flag!='n')
		{
			do
			{
				num = num*10 + Character.getNumericValue(opstr.charAt(idx));
				if(idx+1 >= opstr.length())break;
				idx+=1;
				char next = opstr.charAt(idx);
				numflag = false;
				if(next >= '0' && next <= '9')numflag = true;
			}
			while(numflag);
			opstr = opstr.substring(idx+1, opstr.length());
			--num;
			if(num<0)return;
		}
		switch(flag)
		{
		case 'n' /*op:s 座席の数*/ : op.setSeatNum(Integer.parseInt(opstr));					break;
		case 'x' /*op:s 座席の横*/ : op.offsetX[num] = Integer.parseInt(opstr) * 0.01f;		break;
		case 'y' /*op:s 座席の高さ*/: op.offsetY[num] = Float.parseFloat(opstr) * 0.01f;		break;
		case 'z' /*op:s 座席の奥*/ : op.offsetZ[num] = Float.parseFloat(opstr) * 0.01f;		break;
		case 'r' /*op:s 座席の回転*/: op.rotZ[num] = Float.parseFloat(opstr) * 0.01f;			break;
		}	
	}
	
	public static void addObj(String filename)
	{
		if(MapModel.containsKey(filename))return;	
		MapModel.put(filename, AdvancedModelLoader.loadModel(new ResourceLocation(filename)));
	}
//	public static void addObjCustom(String filename)
//	{
//		if(MapModel.containsKey(filename))return;	
//		MapModel.put(filename, new ercWavefrontObject(new ResourceLocation(filename)));
//	}
	
	public static void addTexture(String filename)
	{
		if(MapTex.containsKey(filename))return;	
 		MapTex.put(filename, new ResourceLocation(filename));
	}
	
	public static IModelCustom getObj(String filename)
	{
		return MapModel.get(filename);
	}
	public static ResourceLocation getTex(String filename)
	{
		return MapTex.get(filename);
	}
	
//	public static ModelPack2 addModelPack2(String MainObjName, String MainTexName, String ConnectObjName, String ConnectTexName, String IconName, String connectIconName)
//	{
//		ERC_Logger.info("additional model register : "+MainObjName+", "+MainTexName+", "+ConnectObjName+", "+ConnectTexName+", "+IconName);
//		ModelPack2 modelpack = new ModelPack2(getObj(MainObjName), getTex(MainTexName), getObj(ConnectObjName), getTex(ConnectTexName), IconName, connectIconName);
////		ModelPackList.add(modelpack);
//		return modelpack;
//	}

	public static void addRailModelPack(String[] ObjNames, String[] TexNames, String iconName)
	{
		if(ObjNames.length != TexNames.length)return;
		IModelCustom[] models = new IModelCustom[ObjNames.length];
		ResourceLocation[] texs = new ResourceLocation[TexNames.length];
		for(int i=0;i<ObjNames.length;++i)models[i] = getObj(ObjNames[i]);
		for(int i=0;i<TexNames.length;++i)texs[i] = getTex(TexNames[i]);
		RailPack railpack = new RailPack(models, texs, iconName);
		RailPackList.add(railpack);
	}
	
	public static ERC_ModelCoaster getModel(int Index, int CoasterType)
	{
		ModelPack mp = null;
		switch(CoasterType)
		{
		case 1 : 
			Index %= ModelPackList_Connect.size();
			mp = ModelPackList_Connect.get(Index);
			break;
		case 2 : 
			Index %= ModelPackList_Mono.size();
			mp = ModelPackList_Mono.get(Index);
			break;
		case 0 : 
		default : 
			Index %= ModelPackList_Main.size();
			mp = ModelPackList_Main.get(Index);
			break;
		}
		return new ERC_ModelCoaster(mp.Model, mp.Tex);
	}
	
	public static ModelOptions getModelOP(int Index, int CoasterType)
	{
		if(Index < 0)Index = 0;
		switch(CoasterType)
		{
		case 1 : 
			Index %= ModelPackList_Connect.size();
			return ModelPackList_Connect.get(Index).op;
		case 2 : 
			Index %= ModelPackList_Mono.size();
			return ModelPackList_Mono.get(Index).op;
		case 0 : 
		default : 
			Index %= ModelPackList_Main.size();
			return ModelPackList_Main.get(Index).op;
		}
	}
	
	public static ERC_ModelAddedRail getRailModel(int Index, int flag)
	{
		if(Index < 0 || Index >= RailPackList.size())Index = 0;
		if(flag < 0 || flag >= 6)flag = 0;
		return RailPackList.get(Index).getModelSet(flag);
	}
	
	public static Wrap_RailRenderer createRailRenderer(int Index, Wrap_TileEntityRail tile)
	{
		if(Index <= 0 || Index > 6) return new ERC_ModelDefaultRail();
		int flag = 0;
		if		(tile instanceof TileEntityRailRedstoneAccelerator)flag = 1;
		else if (tile instanceof TileEntityRailConstVelosity)flag = 2;
		else if (tile instanceof TileEntityRailDetector)flag = 3;
		else if (tile instanceof TileEntityRailBranch2)flag = 4;
		else if (tile instanceof TileEntityRailInvisible)flag = 5;
		else if (tile instanceof TileEntityRailBase)flag = 0;
		return getRailModel(Index-1, flag);
	}
	
	public static String[] getCoasterIconStrings(int CoasterType)
	{
		String[] ret;
		List<ModelPack> list = null;
		switch(CoasterType)
		{
		case 0 : list = ModelPackList_Main; break;
		case 1 : list = ModelPackList_Connect; break;
		case 2 : list = ModelPackList_Mono; break;
		}
		ret = new String[list.size()];
		int idx=0;
		for(ModelPack mp : list)
		{
			ret[idx++] = mp.IconStr;
		}
		return ret;
	}
	
	public static String[] getRailIconStrings()
	{
		String[] ret = new String[RailPackList.size()+1];
		ret[0] = "erc:railicondef";
		int idx = 1;
		for(RailPack rp : RailPackList)
		{
			ret[idx++] = rp.IconStr;
		}
		return ret;
	}
	
	public static int getModelPackNum(int CoasterType)
	{
		switch(CoasterType)
		{
		case 1 : return ModelPackList_Connect.size();
		case 2 : return ModelPackList_Mono.size();
		default :
		case 0 : return ModelPackList_Main.size();
		}
		
	}
	
	public static int getRailPackNum()
	{
		return RailPackList.size();
	}
	
	// 拡張子切り取りとかオプション切り取りとかに
	private static String removeExtention(String fileName)
	{
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(0, point);
	    } 
	    return fileName;
	}
	// オプションの抜き出し
	private static String getExtention(String fileName)
	{
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(point+1, fileName.length());
	    } 
	    return null; //オプションがもうなかったらここ
	}
	
	
	private static ModelPack makeModelPack(ERC_ModelLoadPlan mp)
	{
		ERC_Logger.info("additional model is registed : "+mp.getModelName()+", "+mp.getTextureName()+", "+mp.getIconName()+".png");	
		try
		{
			addObj(mp.getModelName());
		}
		catch (ModelFormatException e)
		{			
			ERC_Logger.warn("can't load model '"+mp.getModelName()+"'");
			ERC_Logger.warn(e.getMessage());
			return null;
		}
		addTexture(mp.getTextureName());
//		addTexture(mp.getIconName());		
		ModelPack modelpack = new ModelPack(getObj(mp.getModelName()), getTex(mp.getTextureName()), mp.getIconName(), mp.getOption());
		return modelpack;
	}
	
	//API
	public static boolean registerCoaster(ERC_ModelLoadPlan mp)
	{
		ModelPack modelpack = makeModelPack(mp);
		if(modelpack == null) return false;
		ModelPackList_Main.add(modelpack);
		return true;
	}
	
	public static boolean registerConnectionCoaster(ERC_ModelLoadPlan mp)
	{
		ModelPack modelpack = makeModelPack(mp);
		if(modelpack == null) return false;
		ModelPackList_Connect.add(modelpack);
		return true;
	}
	
	public static boolean registerMonoCoaster(ERC_ModelLoadPlan mp)
	{ 
		if(mp.getOption().SeatNum != 1)return false;
		ModelPack modelpack = makeModelPack(mp);
		if(modelpack == null) return false;
		ModelPackList_Mono.add(modelpack);
		return false; 
	}
	
}
