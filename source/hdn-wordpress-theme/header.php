<?php include_once "head.php"; ?>

	<body <?php body_class();?>>
		<header>
			<div class="wrapper cf">
				<a href="/" class="site-logo">
					<img src="<?php bloginfo('template_directory'); ?>/img/dot.gif" height="1" width="1" alt="HDN - Health Developers Network" class="logo">
				</a>

				<nav class="beta_nav cf">
					<ul>
						<li><a href="/about">About</a></li>
						
					</ul>
					<div class="search_box">
						<?php include_once "searchform.php"; ?>
					</div><!--end search_box-->
				</nav>
				
				
				
				<div class="nhs_network">
					<p>an <span class="nhs">NHS</span> Network</p>
				</div>
				
				<a href="/" class="mob_nav_btn"><span class="mob_nav_icon" aria-hidden="true" data-icon="&#x3b;"></span></a>	
			</div><!--end wrapper-->
			
			<nav class="main_nav">
				<div class="wrapper">
					<ul>
						<li class="library"><a href="/library">Library</a></li>
						<li class="downloads"><a href="/downloads-data">Downloads &amp; Data</a></li>
						<li class="test"><a href="/testcentre">Test Centre</a></li>
						<li class="learn"><a href="/learn">Learn</a></li>
						<li class="community"><a href="/community">Community</a></li>
						<li class="market"><a href="/community-forums/forum/market-square">Market Square</a></li>
						<li class="contributors"><a href="/contributors">Contributors</a></li>
					</ul>
					
				</div><!--end wrapper-->
			</nav>
		</header>
			