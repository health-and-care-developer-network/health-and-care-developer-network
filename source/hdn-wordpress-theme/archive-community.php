<?php get_header(); ?>
		
		<div role="main" class="main">
			<div class="page_title">
				<div class="wrapper">
					<hgroup>
						<h1>Community</h1>
						<h2>Read blogs, news and events; participate in forums</h2>
					</hgroup>
				</div>
			</div><!--end page title-->

			<div class="main_content cf">
				<?php include_once "breadcrumbs.php"; ?>
				<div class="wrapper cf">
					<div class="fw_nav_boxes">
					
					
					<div class="one_third">
						<div class="box">
							<div class="line aquaGreen_line"></div>
							<section class="nav_box aquagreen cf">
								<div class="comm_box_cont cf">
									<h3>Forums</h3>
									<p>Join the conversation, post questions get answers.</p>
									
								</div><!--end comm_box_cont-->
								<a href="<?php bloginfo('siteurl'); ?>/community-forums" class="btn aquaGreen_btn">Visit Forums <span class="btn_icon" aria-hidden="true" data-icon="&#x29;"></span></a>
							</section>
						</div>
					</div>

					<div class="one_third">
						<div class="box">
							<div class="line aquaGreen_line"></div>
							<section class="nav_box aquagreen cf">
								<div class="comm_box_cont cf">
									<h3>Blogs</h3>
									
									<?php query_posts( array( 'post_type' => 'community', 'orderby' => 'title', 'community-type' => 'community-blog' ) ); ?>
									<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
							
									<article>
										<h4><a href="<?php the_permalink(); ?>"><?php the_title(); ?></a></h4>
										<p class="meta"><?php the_time('j M Y'); ?> - <?php the_author(); ?> </p>
										<?php the_excerpt(); ?>
									</article>
								
									<?php endwhile; endif; wp_reset_query(); ?>	

								</div><!--end comm_box_cont-->
								<a href="blogs" class="btn aquaGreen_btn">Browse Blogs <span class="btn_icon" aria-hidden="true" data-icon="&#x29;"></span></a>
							</section>
						</div>
					</div>

					<div class="one_third">
						<div class="box">
							<div class="line aquaGreen_line"></div>
							<section class="nav_box aquagreen cf">
								<div class="comm_box_cont cf">
									<h3>News &amp; Events</h3>
									
									<?php query_posts( array( 'post_type' => 'community', 'community-type' => 'community-event' ) ); ?>
									<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
							
							
									<article>
										<h4><a href="<?php the_permalink(); ?>"><?php the_title(); ?></a></h4>
										<p class="meta">Posted on: <?php the_time('j M Y'); ?></p>
									</article>
								
								
									<?php endwhile; endif; wp_reset_query(); ?>	
									
								</div><!--end comm_box_cont-->
								<a href="news-events" class="btn aquaGreen_btn">Browse News &amp; Events <span class="btn_icon" aria-hidden="true" data-icon="&#x29;"></span></a>
							</section>
						</div>
					</div>

					
					
					</div><!--end content wrap-->	
				</div><!--end wrapper-->
			</div><!--end main_content-->
<?php get_footer(); ?>