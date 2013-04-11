/* Author:

*/

$(function() {
	var btn 		= $('.mob_nav_btn');
		menu 		= $('.main_nav ul');
		menuHeight	= menu.height();

	$(btn).on('click', function(e) {
		e.preventDefault();
		menu.slideToggle();
	});
});

$(window).resize(function(){
	var w = $(window).width();
	if(w > 741 && menu.is(':hidden')) {
		menu.removeAttr('style');
	}
});	


$(document).ready(function() {
          
  $('ul li.parent').children('.closed').addClass('expand');

    $(".tree_nav li a.expand").toggle(           
    
          function() { // START FIRST CLICK FUNCTION
              $(this).siblings('ul').slideDown()
              if ($(this).hasClass('closed')) {   
                  $(this).removeClass('closed').addClass('open');
              }
          }, // END FIRST CLICK FUNCTION
          
          function() { // START SECOND CLICK FUNCTION
              $(this).siblings('ul').slideUp()

              if ($(this).hasClass('open')) {
                  $(this).removeClass('open').addClass('closed');
              }
          } // END SECOND CLICK FUNCTIOn
    ); // END TOGGLE FUNCTION 

}); // END DOCUMENT READY




var $container = $('.fw_nav_boxes')
// initialize Isotope
$container.isotope({
  // options...
  resizable: false, // disable normal resizing
  // set columnWidth to a percentage of container width
  masonry: { columnWidth: $container.width() / 3 }
});

// update columnWidth on window resize
$(window).smartresize(function(){
  $container.isotope({
    // update columnWidth to a percentage of container width
    masonry: { columnWidth: $container.width() / 3 }
  });
});

$(document).ready(function(){

  var highestBox = 0;
      $('.cat_box, .nav_boxes .nav_box').each(function(){  
              if($(this).height() > highestBox){  
              highestBox = $(this).height();  
      }
  });    
  $('.cat_box').height(highestBox);

});

$(document).ready(function(){

setTimeout(function (){
  if (document.documentElement.clientWidth > 550) {
    $('.container').equalHeights();
  }

  if (screen.width >= 550) {
    $('.container').equalHeights();
  }
}, 700);
});

$(document).ready(function() {
   $('.art_txt a').each(function () {
    if ($(this).has('img').length) {
        $(this).fancybox();
    }
});
});


// Custom Login/Register/Password Code @ http://digwp.com/2010/12/login-register-password-code/
// jQuery
  $(document).ready(function() {
    $(".tab_content_login").hide();
    $("ul.tabs_login li:first").addClass("active_login").show();
    $(".tab_content_login:first").show();
    $("ul.tabs_login li").click(function() {
      $("ul.tabs_login li").removeClass("active_login");
      $(this).addClass("active_login");
      $(".tab_content_login").hide();
      var activeTab = $(this).find("a").attr("href");
      if ($.browser.msie) {$(activeTab).show();}
      else {$(activeTab).show();}
      return false;
    });
  });
