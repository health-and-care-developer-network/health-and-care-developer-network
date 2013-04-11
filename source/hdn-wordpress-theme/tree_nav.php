<nav class="tree_nav">
	<ul>
		<?php 
			$my_pages = wp_list_pages('echo=0&title_li=&post_type=library&depth=0');
		    $var1 = '<a';
		    $var2 = '<a href="" class="closed"></a><a';
		    $my_pages = str_replace($var1, $var2, $my_pages);
		    echo $my_pages;
		?>
	</ul>
</nav><!--end tree nav-->