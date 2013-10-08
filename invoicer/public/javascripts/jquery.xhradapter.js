/**
 * XHR adapter for jQuery.
 *
 * Make it possible to submit forms via XHR to the URL with a given HTTP method
 * instead of building custom event handlers for every form.
 *
 * Put a data-remote="true" attribute on a <form> element to enable the adapter
 * functionality. If provided, the server will interpret the request as
 * "application/javascript" and render a JS template if available.
 */
(function($) {
	var	adapter,
			$document = $(document);


	$.adapter = adapter = {
		formSubmitSelector: 'form[data-remote]',
		linkSelector: 'a[data-remote]'
	};

	/*
		Do the actual XHR.
	 */
	function handleRemote(element) {
		var dataType = "script", method, url, data;

		if(element.is("form")) {
			method = element.attr("method");
			url = element.attr("action");
			data = element.serializeArray();
		}
		else {
			method = element.data("method");
			url = element.attr("href");
			data = element.data("params") || null;
		}

		options = {
			type: method || "GET", 
			data: data, 
			dataType: dataType,

			success: function(data, status, xhr) {
				element.trigger("ajax:success", [data, status, xhr]);
			},
			complete: function(data, status) {
				element.trigger("ajax:complete", [data, status]);
			},
			error: function(data, status, error) {
				element.trigger("ajax:error", [data, status, error]);
			}
		};

		if (url) { options.url = url; }

		var jqXhr = $.ajax(options);
		element.trigger("ajax:send", jqXhr);
	}

	$document.delegate(adapter.formSubmitSelector, "submit", function(evt) {
		var form = $(this),
				remote = form.data("remote") !== undefined;

		if(remote) {
			evt.preventDefault();
			handleRemote(form);
		}
	});

	$document.delegate(adapter.linkSelector, "click", function(evt) {
		var $link = $(this),
				method = $link.data("method");

		if($link.data("remote") !== undefined) {
			if ( (evt.metaKey || evt.ctrlKey) && (!method || method === 'GET')) { return true; }
			evt.preventDefault();
			handleRemote($link);
		}
	});

})(jQuery);