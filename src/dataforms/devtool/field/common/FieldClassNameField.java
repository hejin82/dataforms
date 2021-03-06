package dataforms.devtool.field.common;

import dataforms.field.base.Field;

/**
 * フィールドクラス名フィールドクラス。
 *
 */
public class FieldClassNameField extends SimpleClassNameField {
	/**
	 * autocompleteの例外文字列。
	 */
	private static final String EXCEPTION_PATTERN = "dataforms\\.field\\.sqlfunc";
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "フィールドクラス名";
	/**
	 * コンストラクタ。
	 */
	public FieldClassNameField() {
		this.setBaseClass(Field.class);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
		this.addExceptionPattern(EXCEPTION_PATTERN);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FieldClassNameField(final String id) {
		super(id);
		this.setBaseClass(Field.class);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
		this.addExceptionPattern(EXCEPTION_PATTERN);
	}
}
