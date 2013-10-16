/*
	Invoicer JS tests
	Deps: [Mocha,js, Chai.js, jQuery.js]
	@author Johan Brook
*/

var expect = chai.expect;

describe("Notification", function() {
	afterEach(function() {
		$("#notification").remove();
	});

	it("should have a default type set", function() {
		var n1 = new Notification("Test");
		var n2 = new Notification("Test", {delay: 2000});

		expect(n1.settings.type).to.equal("notice");
		expect(n2.settings.type).to.equal("notice");
	});

	it("should have a type class", function() {
		var n1 = new Notification("Test", "notice");
		expect(n1.element).to.have.class("notice");
	});

	it("should be visible on show()", function() {
		var n1 = new Notification("Test");
		n1.show();
		expect(n1.element).to.be.visible;
	});

	it("should contain text when shown", function() {
		var n1 = new Notification("Test");
		n1.show();
		expect(n1.element).to.have.text("Test");
	});


	it("should have cleared text when hidden", function(done) {
		var n1 = new Notification("Test");
		n1.show();

		expect(n1.element).to.have.text("Test");

		var spy = chai.spy(function() {
					expect(spy).to.have.been.called();
					expect(n1.element).to.have.text("");
					done();
				});

		n1.hide();

		$(n1.element).on("notification:hidden", spy);
	});

	it("should hide after timeout on reveal", function(done) {
		var delay = 1000,
				n1 = new Notification("Test", {delay: delay}),
				spy = chai.spy(function() {
					expect(spy).to.have.been.called();
					done();
				});

		$(n1.element).on("notification:hide", spy);
		n1.reveal();
	});

	it("should trigger events on show/hide", function() {
		var n1 = new Notification("Test");
		var spy = chai.spy();

		n1.element.on("notification:show", spy);
		n1.element.on("notification:hide", spy);
		
		n1.show();
		n1.hide();

		expect(spy).to.have.been.called.twice;
	});
});


describe("Util", function() {

	it("should be in global namespace", function() {
		expect(window.Util).not.to.be.undefined;
	});

	it("should be able to template", function() {
		var template = "This is a template value: {{value}}";
		var data = {
			value: "wohoo"
		};

		var rendered = Util.template(template, data);
		expect(rendered).to.equal("This is a template value: wohoo");
	});

	it("should decrement counter properly", function() {
		var $counter = $("<div />", {text: "1"});

		Util.setCounterElement($counter);
		Util.decrementInvoices();

		expect($counter).to.have.text("0");
	});

	it("should increment counter properly", function() {
		var $counter = $("<div />", {text: "1"});

		Util.setCounterElement($counter);
		Util.incrementInvoices();

		expect($counter).to.have.text("2");
	});

	it("should have dynamically generated notification methods", function() {
		expect(Util.showError).not.to.be.undefined;
		expect(Util.showSuccess).not.to.be.undefined;
		expect(Util.showNotice).not.to.be.undefined;
	});

	it("should show showError()", function() {
		var not = Util.showError("Test");

		expect(not).not.to.be.undefined;
		expect(not.element).to.be.visible;
		expect(not.element).to.have.text("Test");
		expect(not.settings.type).to.equal("negative");
	});

	it("should show showNotice()", function() {
		var not = Util.showNotice("Test");

		expect(not).not.to.be.undefined;
		expect(not.element).to.be.visible;
		expect(not.element).to.have.text("Test");
		expect(not.settings.type).to.equal("notice");
	});

	it("should show showError()", function() {
		var not = Util.showSuccess("Test");

		expect(not).not.to.be.undefined;
		expect(not.element).to.be.visible;
		expect(not.element).to.have.text("Test");
		expect(not.settings.type).to.equal("positive");
	});
});

