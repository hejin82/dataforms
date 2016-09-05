package dataforms.app.dao.user;

import java.util.Map;

import dataforms.app.field.user.LoginIdField;
import dataforms.app.field.user.MailAddressField;
import dataforms.app.field.user.PasswordField;
import dataforms.app.field.user.UserIdField;
import dataforms.app.field.user.UserNameField;
import dataforms.dao.Table;
import dataforms.field.common.DeleteFlagField;

/**
 * ユーザテーブルクラス。
 * <pre>
 * 必要最小限のユーザ情報を記録します。
 * 追加項目が必要な場合、別テーブルを作成してください。
 * </pre>
 */
public class UserInfoTable extends Table {
	/**
	 * コンストラクタ.
	 */
	public UserInfoTable() {
		this.setComment("ユーザ情報テーブル");
		this.addPkField(new UserIdField());
		this.addField(new LoginIdField());
		this.addField(new PasswordField());
		this.addField(new UserNameField());
		this.addField(new MailAddressField());
		this.addField(new DeleteFlagField());
		this.addUpdateInfoFields();
		this.setAutoIncrementId(true);
		this.setSequenceStartValue(Long.valueOf(1000));
	}

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		UserInfoTableRelation r = new UserInfoTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}
	
	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends dataforms.dao.Entity {
		/** ユーザIDのフィールドID。 */
		public static final String ID_USER_ID = "userId";
		/** ログインIDのフィールドID。*/
		public static final String ID_LOGIN_ID = "loginId";
		/** パスワードのフィールドID。*/
		public static final String ID_PASSWORD = "password";
		/** ユーザ名のフィールドID。*/
		public static final String ID_USER_NAME = "userName";
		/** メールアドレスのフィールドID。*/
		public static final String ID_MAIL_ADDRESS = "mailAddress";
		/**
		 * コンストラクタ。
		 * @param map 操作対象マップ。
		 */
		public Entity(final Map<String, Object> map) {
			super(map);
		}
		
		/**
		 * ユーザIDを取得します。
		 * @return ユーザID。
		 */
		public Long getUserId() {
			return (Long) this.getMap().get(ID_USER_ID);
		}
		
		/**
		 * ユーザIDを設定します。
		 * @param userId ユーザID。
		 */
		public void setUserId(final Long userId) {
			this.getMap().put(ID_USER_ID, userId);
		}

		/**
		 * ログインIDを取得します。 
		 * @return ログインID。
		 */
		public String getLoginId() {
			return (String) this.getMap().get(ID_LOGIN_ID);
		}

		/**
		 * ログインIDを設定します。
		 * @param loginId ログインID。
		 */
		public void setLoginId(final String loginId) {
			this.getMap().put(ID_LOGIN_ID, loginId);
		}

		/**
		 * パスワードを取得します。
		 * @return パスワード。
		 */
		public String getPassword() {
			return (String) this.getMap().get(ID_PASSWORD);
		}


		/**
		 * パスワードを設定します。
		 * @param password パスワード。
		 */
		public void setPassword(final String password) {
			this.getMap().put(ID_PASSWORD, password);
		}

		/**
		 * メールアドレスを取得します。
		 * @return メールアドレス。 
		 */
		public String getMailAddress() {
			return (String) this.getMap().get(ID_MAIL_ADDRESS);
		}

		/**
		 * メールアドレスを設定します。
		 * @param mailAddress メールアドレス。
		 */
		public void setMailAddress(final String mailAddress) {
			this.getMap().put(ID_MAIL_ADDRESS, mailAddress);
		}
	}
	
	/**
	 * ユーザIDフィールドを作成します。
	 * @return  ユーザIDフィールド。
	 */
	public UserIdField getUserIdField() {
		return (UserIdField) this.getField(Entity.ID_USER_ID);
	}
	
	/**
	 * ログインIDフィールドを作成します。
	 * @return  ログインIDフィールド。
	 */
	public LoginIdField getLoginIdField() {
		return (LoginIdField) this.getField(Entity.ID_LOGIN_ID);
	}

	/**
	 * パスワードフィールドを取得します。
	 * @return パスワードフィールド。
	 */
	public PasswordField getPasswordField() {
		return (PasswordField) this.getField(Entity.ID_PASSWORD);
	}

	/**
	 * ユーザ名フィールドを取得します。
	 * @return ユーザ名フィールド。
	 */
	public UserNameField getUserNameField() {
		return (UserNameField) this.getField(Entity.ID_USER_NAME);
	}

	/**
	 * メールアドレスフィールド。
	 * @return メールアドレスフィールド。
	 */
	public MailAddressField getMailAddressField() {
		return (MailAddressField) this.getField(Entity.ID_MAIL_ADDRESS);
	}

}
