<!doctype html>
<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	
		<title><?php bloginfo('name'); ?> <?php wp_title('-');?></title>
		<meta name="Content-Type" content="<?php bloginfo('html_type'); ?>"/>
		<meta name="viewport" content="width=device-width,initial-scale=1">
	
		<link rel="stylesheet" href="<?php bloginfo('stylesheet_url');?>"/>


		<!--[if lt IE 8]>
			<link rel="stylesheet" href="<?php bloginfo('template_directory'); ?>/ie.css">
		<![endif]-->
		
		<!-- <link rel="stylesheet" href="<?php bloginfo('template_directory'); ?>/css/print.css"> -->
		<link href='http://fonts.googleapis.com/css?family=Ubuntu:300,400,500' rel='stylesheet' type='text/css'>
		<link rel="shortcut icon" href="<?php bloginfo('stylesheet_directory'); ?>/favicon.ico" />
		
		<?php wp_head(); ?> 
		<script src="<?php bloginfo('template_directory'); ?>/js/libs/modernizr-2.5.3.min.js"></script>
		<?php wp_print_scripts(); ?>

		<!-- Google Analytics code-->
		<script type="text/javascript">
			var _gaq = _gaq || [];
			  _gaq.push(
				  ['_setAccount', 'UA-13017909-3'],
				  ['_trackPageview'],
				  ['b._setAccount', 'UA-38028819-4'],
				  ['b._trackPageview']
				);
			(function() {
				var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
			    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
			    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
			  })();
		</script>
		<link rel="stylesheet" href="<?php bloginfo('template_directory'); ?>/css/fancybox/fancybox.css">
	</head>