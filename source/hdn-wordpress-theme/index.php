<?php get_header(); ?>
		
		<div role="main" class="main">
			<div class="hp_page_title">
				<div class="wrapper">
					<hgroup>
						<h1>Welcome to the Health Developer Network</h1>
						<h2>Information and tools to help developers create software for health and social care.</h2>
					</hgroup>
					<p>
					<a href="/about" class="btn red_btn">Start here <span class="btn_icon" aria-hidden="true" data-icon="&#x29;"></span></a>
					or select one of the sections below.</p>
				</div>
			</div><!--end page title-->
			
			<div class="main_content cf">
				<div class="wrapper cf">
					<div class="nav_boxes">

						<div class="one_third">
							<a href="/library" class="box">
								<div class="line green_line"></div>
								<section class="nav_box green">
									<h2><span class="lg_icon" aria-hidden="true" data-icon="&#x22;"></span><br />Library</h2>
									<p>Browse and search developer information</p>
								</section>
							</a>
						</div>
						
						<div class="one_third">
							<a href="/downloads-data" class="box">
								<div class="line red_line"></div>
								<section class="nav_box red">
									<h2><span class="lg_icon" aria-hidden="true" data-icon="&#x24;"></span><br />Downloads &amp; Data</h2>
									<p>Download tools and source code; access data sources and services</p>
								</section>
							</a>
						</div>
						
						<div class="one_third">
							<a href="/testcentre" class="box">
								<div class="line purple_line"></div>
								<section class="nav_box purple">
									<h2><span class="lg_icon" aria-hidden="true" data-icon="&#x61;"></span><br />Test Centre</h2>
									<p>Find testing tools, data and guidance</p>
								</section>
							</a>
						</div>
						
						<div class="clear"></div>
						
						<div class="one_third">
							<a href="/learn" class="box">
								<div class="line orange_line"></div>
								<section class="nav_box orange">
									<h2><span class="lg_icon" aria-hidden="true" data-icon="&#x26;"></span><br />Learn</h2>
									<p>Explore development tutorials, how-tos and case studies</p>
								</section>
							</a>
						</div>
						
						<div class="one_third">
							<a href="/community" class="box">
								<div class="line aquaGreen_line"></div>
								<section class="nav_box aquagreen">
									<h2><span class="lg_icon" aria-hidden="true" data-icon="&#x21;"></span><br />Community</h2>
									<p>Read blogs, news and events; participate in forums</p>
								</section>
							</a>
						</div>
						
						<div class="one_third">
							<a href="community-forums/forum/market-square/" class="box">
								<div class="line aquaBlue_line"></div>
								<section class="nav_box aquablue">
									<h2><span class="lg_icon" aria-hidden="true" data-icon="&#x27;"></span><br />Market Square</h2>
									<p>Share ideas, problems and expertise</p>
								</section>
							</a>
						</div>
					</div><!--end nav boxes-->
				
					<div class="hp_sidebar">
						<section class="community_latest aquagreen cf">
							<h3>Latest from the community</h3>

							<?php query_posts( array( 'post_type' => 'community', 'community-type' => 'community-blog', 'posts_per_page' => 2 ) ); ?>
							<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>

							<article>
								<header>
									<h4><?php the_title(); ?></h4>
									<p class="meta">
										<time><?php the_time('j M Y'); ?></time> - by <?php the_author(); ?>
									</p>
								</header>
								<?php the_excerpt(); ?>
								<a href="<?php the_permalink(); ?>" class="read_more">Read More</a>
							</article>
							
							<?php endwhile; endif; wp_reset_query(); ?>	
							
							<a href="community/blogs/" class="btn aquaGreen_btn">Visit Blogs</a>
						</section>
						
					</div><!--end hp_sidebar-->
					
				</div><!--end wrapper-->
			</div><!--end main_content-->
		</div><!-- end main -->

<?php get_footer(); ?>				