$(function () {
	$('#example').popover({trigger: 'hover', title: 'Testing', content: 'test content'});
	
	$('.clickablemapview map area').click(function (e) {
	  e.preventDefault();
	  //$(this).tab('show');
	  var href = e.target.hash;
	  //Make correct tab active
	  $(".tab-control a[href=" + href + "]").tab('show');
	});
	
	$('.tab-control a').click(function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	});
	
});