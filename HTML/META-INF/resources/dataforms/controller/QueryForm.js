/**
 * @fileOverview {@link QueryForm}クラスを記述したファイルです。
 */


/**
 * @class QueryForm
 *
 * 問い合わせフォームクラス。
 * <pre>
 * 問い合わせの条件を入力するためのフォームです。
 * </pre>
 * @extends Form
 */
QueryForm = createSubclass("QueryForm", {}, "Form");


/**
 * HTMLエレメントとの対応付けを行います。
 * <pre>
 * 以下のボタンが存在した場合、イベント処理を登録します。
 * #queryButton ... 「検索」ボタンの処理.
 * #resetButton ... 「リセット」ボタンの処理.
 * </pre>
 */
QueryForm.prototype.attach = function() {
	Form.prototype.attach.call(this);
	var queryForm = this;
	this.find('#queryButton').click(function() {
		queryForm.query();
		return false;
	});
	this.find('#resetButton').click(function() {
		queryForm.reset();
		return false;
	});
};

/**
 * クエリの実行を行います。
 */
QueryForm.prototype.query = function() {
	var queryForm = this;
	if (queryForm.validate()) {
		queryForm.submit("query", function(result) {
			queryForm.parent.resetErrorStatus();
			if (result.status == ServerMethod.SUCCESS) {
				var resultForm = queryForm.parent.componentMap["queryResultForm"];
				if (resultForm != null) {
					queryForm.showQueryResultForm();
				} else {
					queryForm.showEditForm();
				}
			} else {
				queryForm.parent.setErrorInfo(queryForm.getValidationResult(result), queryForm);
			}
		});
	}
};

/**
 * 問い合わせ結果フォームに入力された検索条件を設定し、検索を行い結果を表示します。
 */
QueryForm.prototype.showQueryResultForm = function() {
	var resultForm = this.parent.componentMap["queryResultForm"];
	if (resultForm != null) {
		this.parent.find("#queryResultForm").show();
		var condition = this.get().serialize();
		resultForm.condition = condition;
		resultForm.changePage();
	}
};

/**
 * 編集フォームに入力された検索条件を設定し、対象データを編集します。
 */
QueryForm.prototype.showEditForm = function() {
	var editForm = this.parent.componentMap["editForm"];
	if (editForm != null) {
		var condition = this.get().serialize();
		editForm.updateData(condition);
//		this.parent.find("#editForm").show();
/*		var condition = this.get().serialize();
		resultForm.condition = condition;
		resultForm.changePage();*/
	}
};