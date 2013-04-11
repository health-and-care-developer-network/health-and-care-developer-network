 <?php
/*
Template Name: z_Community Events
*/
?>

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
				
				<div class="wrapper cf container">
					<div class="one_quarter tree_wrap">
						<div class="left_bar_list">
							<h3>Categories</h3>
							<ul>
							<?php wp_list_categories('taxonomy=community-categories&orderby=name&show_count=1&title_li='); ?>
						</div><!--end left bar list-->
						
						<div class="left_bar_list">
							<h3>Key Words</h3>
							<ul>
								<?php wp_list_categories('taxonomy=community-key-words&orderby=name&show_count=1&title_li='); ?>
							</ul>
						</div><!--end left bar list-->

					</div>
					
					<div class="three_quarters">
						<div class="content_wrap">
							<h2 class="underline">Latest News &amp; Events</h2>
							<?php query_posts( array( 'post_type' => 'community', 'community-type' => 'community-event' ) ); ?>
							<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
							
							
								<article class="blog_post cf">
									<h3><a href="<?php the_permalink(); ?>"><?php the_title(); ?></a></h3>
									<ul class="blog_meta cf">
										<li class="by"><span class="meta_icon" aria-hidden="true" data-icon="&#x23;"></span><?php the_author(); ?> </li>
										<li class="on"><span class="meta_icon" aria-hidden="true" data-icon="&#x25;"></span><?php the_time('j M Y'); ?></li>
										<li class="in"><span class="meta_icon" aria-hidden="true" data-icon="&#x2c;"></span><?php the_terms( $post->ID, 'community-categories', '', '', '' ); ?></li>
										<li class="tags"><span class="meta_icon" aria-hidden="true" data-icon="&#x2e;"></span><?php the_terms( $post->ID, 'community-key-words', '', ', ', '' ); ?></li>
									</ul>
									<?php the_excerpt(); ?>
									<a href="<?php the_permalink(); ?>" class="btn aquaGreen_btn">Read More <span class="btn_icon" aria-hidden="true" data-icon="&#x29;"></span></a>
								</article>
							
							
							<?php endwhile; endif; wp_reset_query(); ?>	
						</div><!--end content wrap-->	
					</div>
				</div><!--end wrapper-->
			</div><!--end main_content-->
		</div><!-- end main -->
		
<?php get_footer(); ?>		