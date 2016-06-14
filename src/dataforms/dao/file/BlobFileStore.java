package dataforms.dao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import dataforms.dao.Dao;
import dataforms.dao.JDBCConnectableObject;
import dataforms.dao.Table;
import dataforms.servlet.DataFormsServlet;
import dataforms.util.FileUtil;
import dataforms.util.ObjectUtil;

/**
 * BLOBファイルストアクラス。
 * <pre>
 * アップロードされたファイルを、テーブル中のBLOBに記録するファイルストアです。
 * BLOB中にはファイル名、ファイルサイズを記録したヘッダ情報を付加して記録します。
 * </pre>
 */
public class BlobFileStore extends FileStore {

	/**
	 * Log.
	 */
	private static Logger log = Logger.getLogger(BlobFileStore.class);

	/**
	 * フィールドへのポインタ。
	 */
	private JDBCConnectableObject jdbcConnectableObject = null;


	/**
	 * コンストラクタ。
	 * @param field ファイルフィールド。
	 */
	public BlobFileStore(final JDBCConnectableObject field) {
		this.jdbcConnectableObject = field;
	}


	/**
	 * 一時ファイルを作成します。
	 * <pre>
	 * アップロードされたファイルを一時的に記録さるための一時ファイルを作成します。
	 * </pre>
	 * @return 一時ファイル。
	 * @throws Exception 例外。
	 */
	private File makeTempFile() throws Exception {
		File tempdir = new File(DataFormsServlet.getTempDir() + "/blobStore");
		if (!tempdir.exists()) {
			tempdir.mkdirs();
		}
		File ret = File.createTempFile("upload", ".tmp", tempdir);
		return ret;
	}

	/**
	 * アップロードファイル情報をBLOB用一時ファイルに記録します。
	 * @param filename ファイル名。
	 * @param length 長さ。
	 * @param is 入力ストリーム。
	 * @return 作成されたファイル。
	 * @throws Exception 例外。
	 */
	private File makeBlobTempFile(final String filename, final long length, final InputStream is) throws Exception {
		BlobFileHeader info = new BlobFileHeader(filename, length);
		byte[] data = ObjectUtil.getBytes(info);
		int hlen = data.length;
		DecimalFormat fmt = new DecimalFormat("00000000");
		String headerLength = fmt.format(hlen);
		File blobFile = this.makeTempFile();
		FileOutputStream os = new FileOutputStream(blobFile);
		try {
			os.write(headerLength.getBytes());
			os.write(data);
			FileUtil.copyStream(is, os);
		} finally {
			os.close();
		}
		log.debug("blobfile=" + blobFile.getAbsolutePath());
		return blobFile;
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * BLOBに保存するためヘッダ情報 + ファイル本体の形式の一時ファイルを作成します。
	 * </pre>
	 */
	@Override
	protected File makeTempFromFileItem(final FileItem fileItem) throws Exception {
		String fileName = FileUtil.getFileName(fileItem.getName());
		long length = fileItem.getSize();
		File file = null;
		InputStream is = fileItem.getInputStream();
		try {
			file = this.makeBlobTempFile(fileName, length, is);
		} finally {
			is.close();
		}
//		this.tempFile = file;
		return file;
	}

	@Override
	public File makeTemp(final String filename, final File orgfile) throws Exception {
		FileInputStream is = new FileInputStream(orgfile);
		File file = null;
		try {
			file = this.makeBlobTempFile(filename, orgfile.length(), is);
		} finally {
			is.close();
		}
		return file;
	}
	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * FileObjectをそのまま返します。
	 * </pre>
	 */
	@Override
	public Object convertToDBValue(final Object fobj) throws Exception {
		return fobj;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * DAOがBLOBを読みFileObjectのインスタンスに変換するため、
	 * colValueはFileObjectのインスタンスが指定される。
	 * </pre>
	 */
	@Override
	public FileObject convertFromDBValue(final Object colValue) throws Exception {
		FileObject v = (FileObject) colValue;
		return v;
	}

	/**
	 * ファイルヘッダを読み込みます。
	 * @param is 入力ストリーム。
	 * @return ファイルヘッダ。
	 * @throws Exception 例外。
	 */
	private BlobFileHeader readBlobFileHeader(final InputStream is) throws Exception {
		byte[] lenbuf = new byte[8];
		is.read(lenbuf);
		int len = Integer.parseInt(new String(lenbuf));
		log.debug("header length=" + len);
		byte[] fileHeaderBuffer = new byte[len];
		is.read(fileHeaderBuffer);
		BlobFileHeader header = (BlobFileHeader) ObjectUtil.getObject(fileHeaderBuffer);
		return header;
	}


	/**
	 * ファイルストアからファイルの情報を取得します。
	 * <pre>
	 * BLOBからファイル名、ファイル長を取得します。
	 * </pre>
	 * @param colValue DBのカラム値。
	 * @return FileObjectのインスタンス。
	 * @throws Exception 例外。
	 */
	public FileObject readFileInfo(final Object colValue) throws Exception {
		FileObject fobj = null;
		InputStream is = (InputStream) colValue;
		if (is != null) {
			try {
				BlobFileHeader header = this.readBlobFileHeader(is);
				fobj = header.newFileObject();
			} finally {
				is.close();
			}
		}
		return fobj;
	}

	/**
	 * ファイルストアからファイルの情報と内容を取得します。
	 * <pre>
	 * BLOBからファイル名、ファイル長を取得した後、ファイル本体を一時ファイルに展開します。
	 * </pre>
	 * @param colValue DBのカラム値。
	 * @return FileObjectのインスタンス。
	 * @throws Exception 例外。
	 */
	public FileObject readFileInfoAndBody(final Object colValue) throws Exception {
		FileObject fobj = null;
		InputStream is = (InputStream) colValue;
		if (is != null) {
			try {
				BlobFileHeader header = this.readBlobFileHeader(is);
				// TODO:ファイルサイズが小さい場合メモリー中に保存することを考える。
				fobj = header.newFileObject();
				File tempFile = this.makeTempFile();
				fobj.setTempFile(tempFile);
				FileOutputStream os = new FileOutputStream(tempFile);
				try {
					FileUtil.copyStream(is, os);
				} finally {
					os.close();
				}
			} finally {
				is.close();
			}
		}
		return fobj;
	}


	@Override
	public FileObject readFileObject(final Map<String, Object> param) throws Exception {
		Dao dao = new Dao(this.jdbcConnectableObject);
		String tblclass = (String) param.get("table");
		@SuppressWarnings("unchecked")
		Class<? extends Table> cls = (Class<? extends Table>) Class.forName(tblclass);
		Table table = cls.newInstance();
		Map<String, Object> data = table.getPkFieldList().convertClientToServer(param);
		FileObject fobj = dao.queryBlobFileObject(table, (String) param.get("fieldId"), data);
		return fobj;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * fobj中の一時ファイルを取得します。
	 * </pre>
	 */
	@Override
	public File getTempFile(final FileObject fobj) {
		return fobj.getTempFile();
	}
}
