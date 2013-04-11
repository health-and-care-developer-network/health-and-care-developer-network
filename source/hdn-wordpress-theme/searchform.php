
<form method="get" id="searchform" action="<?php bloginfo('url'); ?>/">
	<input type="text" value="<?php echo esc_html($s, 1); ?>" name="s" id="s" class="search_box" placeholder="Search Site">
	<input type="submit" id="search_submit" class="search_btn" value="">
</form>